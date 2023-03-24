package ch.opendata.dle.model;

/*
 * Copyright (c) 2022 by Daniel Hasler
 */

/**
 *
 * @author dani
 */
public record DashboardDataJson( Double total_energy_yesterday, Double total_power, long total_chargers, long occupied_chargers) {
    
}
