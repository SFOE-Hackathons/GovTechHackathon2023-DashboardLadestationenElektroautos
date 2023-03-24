/*
 * Copyright (c) 2022 by Daniel Hasler
 */
package ch.opendata.dle.controller;

import ch.opendata.dle.model.IntervalEnergyJson;
import ch.opendata.dle.service.HistogramService;
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
public class HistogramController {
    
    private final HistogramService service;
    
    public HistogramController( HistogramService histogramService) {
        this.service = histogramService;
    }
    
    @GetMapping( "/rest/histogram/month")
    public List<IntervalEnergyJson> getAggregate() throws IOException {
        return this.service.getMonthHistogram();
    }
}
