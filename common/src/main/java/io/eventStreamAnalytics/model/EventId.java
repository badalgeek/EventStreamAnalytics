package io.eventStreamAnalytics.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;

import java.io.Serializable;

/**
 * Created by sbadal on 1/18/16.
 */
public class EventId implements Serializable {

    private String uuId;
    private String customerId;

    public EventId() {
    }

    public EventId(String uuId, String customerId) {
        this.uuId = uuId;
        this.customerId = customerId;
    }

    @DynamoDBHashKey(attributeName = "c")
    public String getCustomerId() {
        return customerId;
    }

    @DynamoDBRangeKey(attributeName = "id")
    public String getUuId() {
        return uuId;
    }

    public void setUuId(String uuId) {
        this.uuId = uuId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

}
