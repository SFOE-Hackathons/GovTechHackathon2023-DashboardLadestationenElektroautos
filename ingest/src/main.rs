use std::alloc::System;
use std::cmp::max;
use std::collections::HashMap;
use std::env;
use std::time::{SystemTime, UNIX_EPOCH};
use std::str::FromStr;

use elasticsearch::{auth::Credentials, BulkOperation, BulkParts, Elasticsearch, http::transport::Transport};
use serde::{Deserialize, Serialize};
use serde_json::{json, Map, Value};
use tokio::time;
use uuid::Uuid;

use dotenv::dotenv;

use crate::EVSEStatus::Occupied;

#[derive(Serialize, Deserialize, Debug)]
struct EVSEDataResponse {
    #[serde(rename = "EVSEData")]
    evse_data: Vec<EVSEDataRecordContainer>
}

#[derive(Serialize, Deserialize, Debug)]
struct EVSEDataRecordContainer {
    #[serde(rename = "EVSEDataRecord")]
    evse_data_record: Vec<EVSEDataRecord>,
}

#[derive(Serialize, Deserialize, Debug)]
struct EVSEDataRecord {
    #[serde(rename = "GeoCoordinates")]
    geo_coordinates: Google,
    #[serde(rename = "lastUpdate")]
    last_update: Option<String>,
    #[serde(rename = "EvseID")]
    evse_id: String,
    #[serde(rename = "Address")]
    address: Address,
    #[serde(rename = "ChargingFacilities")]
    charging_facilities: Vec<Map<String, Value>>
}

#[derive(Serialize, Deserialize, Debug)]
struct Google {
    #[serde(rename = "Google")]
    google: String
}

#[derive(Serialize, Deserialize, Debug)]
#[serde(rename_all = "PascalCase")]
struct Address {
    postal_code: Option<String>
}

#[derive(Serialize, Deserialize, Debug, PartialEq)]
#[serde(rename_all = "PascalCase")]
enum EVSEStatus {
    Available,
    Occupied,
    OutOfService,
    Unknown
}

#[derive(Serialize, Deserialize, Debug)]
#[serde(rename_all = "camelCase")]
struct EVSEStatusResponse {
    #[serde(rename = "EVSEStatuses")]
    evse_statuses: Vec<EVSEStatusRecordContainer>
}

#[derive(Serialize, Deserialize, Debug)]
struct EVSEStatusRecordContainer {
    #[serde(rename = "EVSEStatusRecord")]
    evse_status_record: Vec<EVSEStatusRecord>
}

#[derive(Serialize, Deserialize, Debug)]
#[serde(rename_all = "camelCase")]
struct EVSEStatusRecord{
    #[serde(rename = "EvseID")]
    evse_id: String,
    #[serde(rename = "EVSEStatus")]
    evse_status: EVSEStatus
}

// Elastic Interface!
#[derive(Serialize, Deserialize, Debug, Clone)]
struct Charging {
    start: u128,
    end: Option<u128>,
    nominal_max_power: f64,
    estimated_power: Option<f64>,
    charger: String,
    zip: String,
    location: [f64;2],
    energy: f64,
}

impl Charging {
    pub fn set_end(&mut self, end: u128) {
        self.end = Some(end);
        let duration_millis = self.end.unwrap() - self.start;
        let duration_seconds = duration_millis / 1000;
        let duration_hours: f64 = (duration_seconds as f64) / 3600_f64;
        self.energy = (duration_hours * self.estimated_power.unwrap());
    }
}

// Elastic Interface!
#[derive(Serialize, Deserialize, Debug, Clone)]
struct Realtime {
    last_update: u128,
    occupied: bool,
    nominal_max_power: f64, //grösste
    estimated_power: Option<f64>, //immer min. 11
    zip: String,
    location: [f64;2]
}

enum IntOrString {
    Int(i32),
    String(String),
}

#[tokio::main]
async fn main() {
    dotenv().ok();

    let cloud_id= env::var("ELASTIC_ID").unwrap();
    let credentials = Credentials::Basic(env::var("ELASTIC_USERNAME").unwrap(), env::var("ELASTIC_PASSWORD").unwrap());
    let transport = Transport::cloud(&cloud_id, credentials).unwrap();
    let client = Elasticsearch::new(transport);

    let mut occupied: HashMap<String, Charging> = HashMap::new();

    let mut lookup_table: HashMap<String, LookupTableEntry> = HashMap::new();

    async fn refresh_lookup_table(lookup_table: &mut HashMap<String, LookupTableEntry>) {
        let data_response = fetch_evse_data().await;
        for container in data_response.evse_data {
            for record in container.evse_data_record {
                let coords_str: Vec<&str> = record.geo_coordinates.google.split(' ').collect();
                let coords_flt = [coords_str[0].parse::<f64>().unwrap(), coords_str[1].parse::<f64>().unwrap()];
                let max_nominal_power = record.charging_facilities.into_iter()
                    .fold(0_f64, |prev, next|
                        if let Some(x) = next.get("power") {
                            x.as_f64().unwrap_or(f64::from_str(x.as_str().unwrap_or("")).unwrap_or(11_f64))
                        } else { 11_f64 });
                let estimated_power = 0.6 * if max_nominal_power > 11_f64 {max_nominal_power} else {11_f64};

                let lte = LookupTableEntry{
                    location: coords_flt,
                    zip: record.address.postal_code.unwrap_or(String::from("-")),
                    canton: "BE".to_string(),
                    estimated_power: estimated_power,
                    max_nominal_power: max_nominal_power
                };
                lookup_table.insert(record.evse_id, lte);
            }
        }
    }

    refresh_lookup_table(&mut lookup_table).await;

    loop {
        let mut realtime: HashMap<String, Realtime> = HashMap::new();

        let mut slots: HashMap<String, EVSEStatus> = HashMap::new();
        let mut newly_unoccupied: HashMap<String, Charging> = HashMap::new();

        let status_response = fetch_evse_status().await;
        for container in status_response.evse_statuses {
            for status in container.evse_status_record {
                let lookup_entry = lookup_table.get(&status.evse_id).unwrap();
                realtime.insert(status.evse_id.clone(), Realtime {
                    last_update: now(),
                    occupied: status.evse_status == Occupied,
                    nominal_max_power: lookup_entry.max_nominal_power,
                    estimated_power: Some(lookup_entry.estimated_power.clone()),
                    zip: lookup_entry.zip.clone(),
                    location: lookup_entry.location
                });

                if status.evse_status == Occupied {
                    if let Some(_) = occupied.get(&status.evse_id) {
                        // do nothing, was already occupied
                    } else {
                        // newly occupied
                        occupied.insert(status.evse_id.clone(), Charging {
                            start: now(),
                            end: None,
                            nominal_max_power: lookup_entry.max_nominal_power,
                            estimated_power: Some(lookup_entry.estimated_power.clone()),
                            zip: lookup_entry.zip.clone(),
                            location: lookup_entry.location,
                            charger: status.evse_id.clone(),
                            energy: 0_f64,
                        });
                    }
                } else {
                    if let Some(occ) = occupied.get(&status.evse_id) {
                        let mut occ = occ.clone();
                        occ.set_end(now());
                        newly_unoccupied.insert(status.evse_id.clone(), (occ).clone());
                        occupied.remove(&status.evse_id);
                    } else {
                        // do nothing, was already unocupied
                    }
                }
                slots.insert(status.evse_id, status.evse_status);
            }
        }

        // println!("Newly unoccupied: {:?}", newly_unoccupied);

        let mut ops: Vec<BulkOperation<Value>> = Vec::new();
        for (index, no) in &newly_unoccupied {
            let mut update_map: HashMap<String, Value> = HashMap::new();
            update_map.insert(String::from("doc"), serde_json::to_value(&no).unwrap());
            update_map.insert(String::from("doc_as_upsert"), json!(true));
            let id = Uuid::new_v4();
            ops.push(BulkOperation::from(BulkOperation::update(id.to_string(), serde_json::to_value(&update_map).unwrap())))
        }

        if &newly_unoccupied.len() > &0 {
            let res = client.bulk(BulkParts::Index("charging"))
                .body(ops)
                .send()
                .await
                .unwrap();

            println!("Charging bulk updated: {:?}", res);
        }

        let mut rt_ops: Vec<BulkOperation<Value>> = Vec::new();
        println!("{:?}", realtime);

        for (index, rt) in &realtime{
            let mut update_map: HashMap<String, Value> = HashMap::new();
            update_map.insert(String::from("doc"), serde_json::to_value(&rt).unwrap());
            update_map.insert(String::from("doc_as_upsert"), json!(true));
            rt_ops.push(BulkOperation::from(BulkOperation::update(index.clone(), serde_json::to_value(&update_map).unwrap())))
        }

        let res = client.bulk(BulkParts::Index("realtime"))
            .body(rt_ops)
            .send()
            .await
            .unwrap();

        println!("Realtime bulk updated: {:?}", res);

        time::sleep(time::Duration::from_secs(5)).await
    }
}

struct LookupTableEntry {
    location: [f64; 2],
    zip: String,
    canton: String,
    estimated_power: f64,
    max_nominal_power: f64
}

async fn fetch_evse_data() -> EVSEDataResponse {
    let uri = "https://data.geo.admin.ch/ch.bfe.ladestellen-elektromobilitaet/data/ch.bfe.ladestellen-elektromobilitaet.json";
    let resp = reqwest::get(uri).await.unwrap().json::<EVSEDataResponse>().await.unwrap();
    resp
}

async fn fetch_evse_status() -> EVSEStatusResponse {
    let uri = "https://data.geo.admin.ch/ch.bfe.ladestellen-elektromobilitaet/status/ch.bfe.ladestellen-elektromobilitaet.json";
    let resp = reqwest::get(uri).await.unwrap().json::<EVSEStatusResponse>().await.unwrap();
    resp
}

fn now() -> u128 {
    let now = SystemTime::now();
    now.duration_since(UNIX_EPOCH).unwrap().as_millis()
}