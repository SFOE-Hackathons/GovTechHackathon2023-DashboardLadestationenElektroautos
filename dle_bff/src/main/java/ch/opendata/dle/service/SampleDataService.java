/*
 * Copyright (c) 2022 by Daniel Hasler
 */
package ch.opendata.dle.service;

import java.io.IOException;
import java.util.Date;
import java.util.Random;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author dani
 */
@Service
public class SampleDataService {
   
    private static final Logger logger = LoggerFactory.getLogger( SampleDataService.class);
    private static final String[] cantons = { "BL", "BS", "AG", "SO" };
    
    private final RestHighLevelClient client;
    
    public SampleDataService( RestHighLevelClient client) {
        this.client = client;
    }
    
    public void generate() throws IOException {
        this.logger.info( "Generating sample data.");
        
        var rand = new Random( System.currentTimeMillis());
        
        var br = new BulkRequest( "realtime");
        
        for( int i = 0; i < 1000; i++) {
            var builder = XContentFactory.jsonBuilder();
            
            builder.startObject();

            builder.field( "last_update", new Date());
            builder.field( "nominal_max_power", 150.0);
            builder.field( "estimated_power", 100.0);
            builder.field( "occupied", (rand.nextInt( 5) == 0));
            builder.field( "canton", cantons[ rand.nextInt( cantons.length)]);
            builder.field( "zip", "8001");
            builder.array( "location", -71.34, 41.12);
            
            builder.endObject();
            
//            var ur = new UpdateRequest( "realtime", Integer.toString( i)).upsert( builder);
//            
//            this.client.update( ur, RequestOptions.DEFAULT);

//            var ir = new IndexRequest( "realtime").id( "Charger" + i).source(builder);
//            var ur = new UpdateRequest( "realtime", "Charger" + i).upsert( ir);
            
            var ur2 = new UpdateRequest( "realtime", "Charger" + i).doc( builder).upsert( builder);
//            this.client.index(ir, RequestOptions.DEFAULT);
//            this.client.update(ur2, RequestOptions.DEFAULT);

            br.add( ur2);
        }
        
        this.client.bulk( br, RequestOptions.DEFAULT);
    }    
}
