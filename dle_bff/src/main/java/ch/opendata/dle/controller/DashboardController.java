/*
 * Copyright (c) 2022 by Daniel Hasler
 */
package ch.opendata.dle.controller;

import ch.opendata.dle.model.DashboardDataJson;
import ch.opendata.dle.service.DashboardService;
import java.io.IOException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author dani
 */
@CrossOrigin(origins = "*")
@RestController
public class DashboardController {
    
    private final DashboardService service;
    
    public DashboardController( DashboardService dashboardService) {
        this.service = dashboardService;
    }
    
    @GetMapping( "/rest/dashboard")
    public DashboardDataJson getAggregate() throws IOException {
        return this.service.getDashboard();
    }
}
