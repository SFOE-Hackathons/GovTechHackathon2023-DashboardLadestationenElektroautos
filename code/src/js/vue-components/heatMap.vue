<script>
import Map from "ol/Map";
import View from "ol/View";
import Heatmap from "ol/layer/Heatmap";
import VectorSource from "ol/source/Vector";
import axios from "axios";
import Feature from "ol/Feature";
import Point from "ol/geom/Point";
import MapLibreLayer from "@geoblocks/ol-maplibre-layer";
import ScaleLine from "ol/control/ScaleLine";
import FullScreen from "ol/control/FullScreen";
import Rotate from "ol/control/Rotate";

import "ol/ol.css";

import proj4 from "proj4";

proj4.defs(
  "EPSG:3857",
  "+proj=merc +a=6378137 +b=6378137 +lat_ts=0 +lon_0=0 +x_0=0 +y_0=0 +k=1 +units=m +nadgrids=@null +wktext +no_defs +type=crs"
);

export default {
  name: "MyMapComponent",
  created() {
    axios
      .get(
        "https://data.geo.admin.ch/ch.bfe.ladestellen-elektromobilitaet/data/ch.bfe.ladestellen-elektromobilitaet.json"
      )
      .then((dataResponse) => {
        axios
          .get(
            "https://data.geo.admin.ch/ch.bfe.ladestellen-elektromobilitaet/status/ch.bfe.ladestellen-elektromobilitaet.json"
          )
          .then((statusResponse) => {
            const data = dataResponse["data"]["EVSEData"];
            const statuses = statusResponse["data"]["EVSEStatuses"];
            for (var i = 0; i < data.length; i++) {
              const operator_status = statuses[i]["EVSEStatusRecord"]
                .filter((s) => s["EVSEStatus"] === "Occupied")
                .map((s) => s["EvseID"]);
              const features = data[i]["EVSEDataRecord"]
                .filter((f) => operator_status.includes(f["EvseID"]))
                .map((f) => {
                  const geocoordinates = f["GeoCoordinates"]["Google"].split(
                    " "
                  );
                  const feature = new Feature({
                    geometry: new Point(
                      proj4("EPSG:4326", "EPSG:3857", [
                        parseFloat(geocoordinates[1]),
                        parseFloat(geocoordinates[0]),
                      ])
                    ),
                  });
                  var pow = 0;
                  if (f["ChargingFacilities"]) {
                    pow = Math.max(
                      ...f["ChargingFacilities"].map((a) => a["power"] ?? 0)
                    );
                  }

                  feature.setProperties({
                    power: pow,
                  });

                  return feature;
                });
              this.source.addFeatures(features);
            }
          });
      });
    setInterval(() => {
      axios
        .get(
          "https://data.geo.admin.ch/ch.bfe.ladestellen-elektromobilitaet/data/ch.bfe.ladestellen-elektromobilitaet.json"
        )
        .then((dataResponse) => {
          axios
            .get(
              "https://data.geo.admin.ch/ch.bfe.ladestellen-elektromobilitaet/status/ch.bfe.ladestellen-elektromobilitaet.json"
            )
            .then((statusResponse) => {
              this.source.clear();
              const data = dataResponse["data"]["EVSEData"];
              const statuses = statusResponse["data"]["EVSEStatuses"];
              for (var i = 0; i < data.length; i++) {
                const operator_status = statuses[i]["EVSEStatusRecord"]
                  .filter((s) => s["EVSEStatus"] === "Occupied")
                  .map((s) => s["EvseID"]);
                const features = data[i]["EVSEDataRecord"]
                  .filter((f) => operator_status.includes(f["EvseID"]))
                  .map((f) => {
                    const geocoordinates = f["GeoCoordinates"]["Google"].split(
                      " "
                    );
                    const feature = new Feature({
                      geometry: new Point(
                        proj4("EPSG:4326", "EPSG:3857", [
                          parseFloat(geocoordinates[1]),
                          parseFloat(geocoordinates[0]),
                        ])
                      ),
                    });
                    var pow = 0;
                    if (f["ChargingFacilities"]) {
                      pow = Math.max(
                        ...f["ChargingFacilities"].map((a) => a["power"] ?? 0)
                      );
                    }

                    feature.setProperties({
                      power: pow,
                    });

                    return feature;
                  });
                this.source.addFeatures(features);
              }
            });
        });
    }, 10000);
  },
  methods: {},
  mounted() {
    this.source = new VectorSource();

    const lightBaseMap = new MapLibreLayer({
      id: "lightbasemap",
      maplibreOptions: {
        style:
          "https://cms.geo.admin.ch/www.geo.admin.ch/cms_api/gl-styles/hackathon_ladestat.json",
      },
    });

    const labels = new MapLibreLayer({
      id: "lightbasemap",
      maplibreOptions: {
        style:
          "https://cms.geo.admin.ch/www.geo.admin.ch/cms_api/gl-styles/ladestat_labels.json",
      },
    });

    const vector = new Heatmap({
      source: this.source,
      gradient: ["rgb(255,255,255)", "rgba(213,199,32,0.05)"],
      blur: 8,
      radius: 6,
      weight: function (feature) {
        return feature.getProperties()["power"] / 200;
      },
    });
    const map = new Map({
      target: "map-container",
      layers: [lightBaseMap, vector, labels],
      view: new View({
        center: [915602.81, 5911929.47],
        zoom: 8,
      }),
    });

    map.addControl(new ScaleLine());
    map.addControl(new Rotate());
    map.addControl(new FullScreen());
  },
};
</script>

<template>
  <div
    id="map-container"
    className="min-w-full"
    style="height: 35rem; background-color: black"
  ></div>
</template>

<style scoped></style>
