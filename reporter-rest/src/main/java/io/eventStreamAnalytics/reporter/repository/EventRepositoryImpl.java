package io.eventStreamAnalytics.reporter.repository;

import io.eventStreamAnalytics.dto.UniqueEndUsersCountAtTime;
import io.eventStreamAnalytics.model.Event;
import io.eventStreamAnalytics.dto.UniqueCustomerCountByDeviceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by sandeep on 23/1/16.
 */
@Repository
public class EventRepositoryImpl implements EventDao {

    @Autowired
    MongoTemplate mongoTemplate;

    public List<UniqueCustomerCountByDeviceType> getCustomerCount() {

        Aggregation agg = newAggregation(
                group("deviceId","sessionId"),
                group("deviceId").first("deviceId").as("deviceType").count().as("total")
        );

        //Convert the aggregation result into a List
        AggregationResults groupResults
                = mongoTemplate.aggregate(agg, (Class<?>) Event.class, UniqueCustomerCountByDeviceType.class);
        List<UniqueCustomerCountByDeviceType> result = groupResults.getMappedResults();

        return result;
    }

    public List<UniqueEndUsersCountAtTime> getUniqueEndUsersCountByTime(){
        long midnightInMillis = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond()*1000;
        Aggregation agg = newAggregation(
                match(Criteria.where("dateTimeinMillis").gte(midnightInMillis)),
                group("sessionId").min("dateTimeinMillis").as("time").push("sessionId").as("sessionId"),
                project("sessionId","time").and("time").minus(midnightInMillis).as("time"),
                project("sessionId","time").and("time").divide(1000*60*5).as("time"),
                project("sessionId","time").and("time").mod(1).as("mod"),
                project("sessionId","time").and("time").minus("mod").as("time"),
                group("time").count().as("count").push("time").as("time")
        );


        AggregationResults groupResults
                = mongoTemplate.aggregate(agg, Event.class, UniqueEndUsersCountAtTime.class);
        List<UniqueEndUsersCountAtTime> result = groupResults.getMappedResults();

        return result;
    }


}
