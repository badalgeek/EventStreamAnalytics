package io.eventStreamAnalytics.reporter.repository;

import io.eventStreamAnalytics.model.Event;
import io.eventStreamAnalytics.model.TotalCustomer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

/**
 * Created by sandeep on 23/1/16.
 */
@Repository
public class EventRepositoryImpl implements EventDao {

    @Autowired
    MongoTemplate mongoTemplate;

    public List<TotalCustomer> getCustomerCount() {

        Aggregation agg = newAggregation(
                group("deviceId","sessionId"),
                group("deviceId").first("deviceId").as("deviceId").count().as("total")
        );

        //Convert the aggregation result into a List
        AggregationResults groupResults
                = mongoTemplate.aggregate(agg, (Class<?>) Event.class, TotalCustomer.class);
        List<TotalCustomer> result = groupResults.getMappedResults();

        return result;
    }


}
