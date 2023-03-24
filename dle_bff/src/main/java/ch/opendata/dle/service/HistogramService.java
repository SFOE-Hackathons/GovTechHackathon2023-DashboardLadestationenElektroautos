/*
 * Copyright (c) 2022 by Daniel Hasler
 */
package ch.opendata.dle.service;

import ch.opendata.dle.model.IntervalEnergyJson;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

/**
 *
 * @author dani
 */
@Service
public class HistogramService {
    
    private final RestHighLevelClient client;
    
    public HistogramService( RestHighLevelClient client) {
        this.client = client;
    }

    public List<IntervalEnergyJson> getMonthHistogram() throws IOException {
        var sr = new SearchRequest( "charging");
        var builder = new SearchSourceBuilder();
        
        var today = LocalDate.of( 2023, 3, 18);
        
        var start = today.minus( 10, ChronoUnit.DAYS).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
        var end = today.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
        
        builder.query( QueryBuilders.rangeQuery( "start").gte( new Date( start)).lt( new Date( end)));
//        builder.aggregation( AggregationBuilders.histogram( "total_energy").interval(24).field( "energy").subAggregation( AggregationBuilders.sum( "daily_energy").field( "energy")));
        
        builder.aggregation( AggregationBuilders.dateHistogram( "total_energy").fixedInterval( DateHistogramInterval.DAY).field( "start").subAggregation( AggregationBuilders.sum( "daily_energy").field( "energy")));
        
        sr.source( builder);
        
        var response = client.search( sr, RequestOptions.DEFAULT);
        
        var agg = (ParsedDateHistogram)response.getAggregations().asMap().get( "total_energy");

        return agg.getBuckets().stream().map( b -> (ParsedDateHistogram.ParsedBucket)b)
                .map( pb -> new IntervalEnergyJson( pb.getKeyAsString().substring( 0, 10), ((ParsedSum)pb.getAggregations().asMap().get( "daily_energy")).getValue())).toList();
    }
}
