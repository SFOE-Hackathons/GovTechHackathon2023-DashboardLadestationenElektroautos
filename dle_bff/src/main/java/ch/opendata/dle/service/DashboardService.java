/*
 * Copyright (c) 2022 by Daniel Hasler
 */
package ch.opendata.dle.service;

import ch.opendata.dle.model.DashboardDataJson;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.ParsedValueCount;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

/**
 *
 * @author dani
 */
@Service
public class DashboardService {
    
    private final RestHighLevelClient client;
    
    public DashboardService( RestHighLevelClient client) {
        this.client = client;
    }
    
    public DashboardDataJson getDashboard() throws IOException {
        var sr = new SearchRequest( "realtime");
        var builder = new SearchSourceBuilder(); 
        
        builder.query( QueryBuilders.termQuery( "occupied", Boolean.TRUE));
        builder.aggregation( AggregationBuilders.sum( "total_power").field( "estimated_power"));
        builder.aggregation( AggregationBuilders.count( "occupied_chargers").field( "estimated_power"));
                
        sr.source( builder);
        
        SearchResponse response = client.search( sr, RequestOptions.DEFAULT);
        
        var agg1 = (ParsedSum)response.getAggregations().asMap().get( "total_power");
        var agg2 = (ParsedValueCount)response.getAggregations().asMap().get( "occupied_chargers");
                
        var yesterdayEnergy = getConsumedEnergyYesterday();
        
        return new DashboardDataJson( yesterdayEnergy, agg1.getValue(), 10123l, agg2.getValue());
    }
    
    private double getConsumedEnergyYesterday() throws IOException {
        var sr = new SearchRequest( "charging");
        var builder = new SearchSourceBuilder();
        
        var today = LocalDate.of( 2023, 3, 18);
        
        var start = today.minus( 1, ChronoUnit.DAYS).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
        var end = today.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
        
        builder.query( QueryBuilders.rangeQuery( "start").gte( new Date( start)).lt( new Date( end)));
        builder.aggregation( AggregationBuilders.sum( "total_energy").field( "energy"));
        
        sr.source( builder);
        
        var response = client.search( sr, RequestOptions.DEFAULT);
        
        var agg = (ParsedSum)response.getAggregations().asMap().get( "total_energy");
        
        return agg.getValue();
    }
}
