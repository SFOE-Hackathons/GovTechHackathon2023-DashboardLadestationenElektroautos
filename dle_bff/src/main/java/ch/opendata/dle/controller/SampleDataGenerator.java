/*
 * Copyright (c) 2022 by Daniel Hasler
 */
package ch.opendata.dle.controller;

import ch.opendata.dle.service.SampleDataService;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author dani
 */
@RestController
public class SampleDataGenerator {
    
    private final SampleDataService service;
    
    public SampleDataGenerator( SampleDataService sampleDataService) {
        this.service = sampleDataService;
    }
    
    @GetMapping( "/rest/generate")
    @ResponseStatus( HttpStatus.NO_CONTENT)
    public void generateTestData() throws IOException {
        this.service.generate();
    }
}
