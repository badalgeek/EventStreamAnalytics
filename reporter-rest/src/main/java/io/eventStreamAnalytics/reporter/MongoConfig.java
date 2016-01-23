package io.eventStreamAnalytics.reporter;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.io.IOException;

/**
 * Created by badal on 1/23/16.
 */
@Configuration
public class MongoConfig {

    @Value("${eventStreamAnalytics.mongodb.uri}")
    private String mongoUri;

    @Value("${eventStreamAnalytics.mongodb.database}")
    private String mongoDatabase;

    @Bean(name="mongoClient")
    public MongoClient mongoClient() throws IOException {
        MongoClientURI uri  = new MongoClientURI(mongoUri);
        return new MongoClient(uri);
    }

    @Autowired
    @Bean(name="mongoDbFactory")
    public MongoDbFactory mongoDbFactory(MongoClient mongoClient) {
        return new SimpleMongoDbFactory(mongoClient, mongoDatabase);
    }

    @Autowired
    @Bean(name="mongoTemplate")
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, mongoDatabase);
    }
}
