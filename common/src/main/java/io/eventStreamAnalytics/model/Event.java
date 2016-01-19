package io.eventStreamAnalytics.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.google.common.base.Splitter;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by badal on 1/9/16.
 */
@DynamoDBTable(tableName = "events")
public class Event implements Serializable {

    @Id
    private EventId eventId;
    private String sessionId;
    private String eventName;
    private String url;

    public Event(String message) {
        try {
            Map<String, String> split = Splitter.on("&").withKeyValueSeparator("=").split(message);
            this.eventId = new EventId(split.get("id"), split.get("c"));
            this.sessionId = split.get("s");
            this.eventName = split.get("e");
            this.url = split.get("u");
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public Event() {
    }

    public Event(String customerId, String sessionId, String eventName, String url) {
        this.eventId = new EventId("", customerId);
        this.sessionId = sessionId;
        this.eventName = eventName;
        this.url = url;
    }

    @DynamoDBAttribute(attributeName = "s")
    public String getSessionId() {
        return sessionId;
    }

    @DynamoDBAttribute(attributeName = "e")
    public String getEventName() {
        return eventName;
    }

    @DynamoDBAttribute(attributeName = "u")
    public String getUrl() {
        return url;
    }

    public EventId getEventId() {
        return eventId;
    }

    @DynamoDBHashKey(attributeName = "c")
    public String getCustomerId() {
        return eventId.getCustomerId();
    }

    @DynamoDBRangeKey(attributeName = "id")
    public String getUuId() {
        return eventId.getUuId();
    }

    public void setUuId(String uuId) {
        if (eventId == null) {
            eventId = new EventId();
        }
        this.eventId.setUuId(uuId);
    }

    public void setCustomerId(String customerId) {
        if (eventId == null) {
            eventId = new EventId();
        }
        this.eventId.setCustomerId(customerId);
    }

    public void setEventId(EventId eventId) {
        this.eventId = eventId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String deSerialize() {
        StringBuilder sb = new StringBuilder();
        sb.append("/events?");
        sb.append("c=").append(eventId.getCustomerId());
        sb.append("&s=").append(sessionId);
        sb.append("&e=").append(eventName);
        sb.append("&u=").append(url);
        return sb.toString();
    }

    public Item getItem() {
        return new Item()
                .withPrimaryKey("c", eventId.getCustomerId())
                .withString("id", eventId.getUuId())
                .withString("s", getSessionId())
                .withString("e", getEventName())
                .withString("u", getUrl());
    }

}
