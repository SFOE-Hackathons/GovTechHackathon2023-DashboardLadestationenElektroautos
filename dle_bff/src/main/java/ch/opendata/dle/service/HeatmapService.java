/*
 * Copyright (c) 2022 by Daniel Hasler
 */
package ch.opendata.dle.service;

import ch.opendata.dle.model.ChargerStatusJson;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

/**
 *
 * @author dani
 */
@Service
public class HeatmapService {
    
    private final RestHighLevelClient client;
    
    public HeatmapService( RestHighLevelClient client) {
        this.client = client;
    }

    public List<ChargerStatusJson> getHeatmap() throws IOException {
        var sr = new SearchRequest( "realtime");
        var builder = new SearchSourceBuilder();
        
        builder.query( QueryBuilders.termQuery( "occupied", Boolean.TRUE));
        builder.size( 4000);
                
        sr.source( builder);
        
        var response = client.search( sr, RequestOptions.DEFAULT);
       
        return Arrays.stream( response.getHits().getHits())
                .map( h -> h.getSourceAsMap())
                    .map( s -> new ChargerStatusJson( toDoubleArray( s.get( "location")), (Double)s.get( "estimated_power"))).toList();
    }
    
    private static Double[] toDoubleArray( Object o) {
        return ((List<Double>)o).stream().toArray( i -> new Double[i]);
    }
}
