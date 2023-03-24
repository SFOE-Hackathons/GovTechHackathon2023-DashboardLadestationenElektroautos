/*
 * Copyright (c) 2022 by Daniel Hasler
 */
package ch.opendata.dle.controller;

import ch.opendata.dle.model.ChargerStatusJson;
import ch.opendata.dle.service.HeatmapService;
import java.io.IOException;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author dani
 */
@CrossOrigin(origins = "*")
@RestController
public class HeatmapController {
    
    private final HeatmapService service;
    
    public HeatmapController( HeatmapService histogramService) {
        this.service = histogramService;
    }
    
    @GetMapping( "/rest/heatmap")
    public List<ChargerStatusJson> getAggregate() throws IOException {
        return this.service.getHeatmap();
    }
}
