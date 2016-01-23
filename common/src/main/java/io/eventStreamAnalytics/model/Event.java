package io.eventStreamAnalytics.model;

import com.google.common.base.Splitter;
import org.bson.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by badal on 1/9/16.
 */
@org.springframework.data.mongodb.core.mapping.Document(collection = "events")
public class Event implements Serializable {

    @Id
    @Field(value = "id")
    private String uuId;
    @Field(value = "c")
    private String customerId;
    @Field(value = "s")
    private String sessionId;
    @Field(value = "e")
    private String eventName;
    @Field(value = "u")
    private String url;

    public Event() {
    }

    public Event(String message) {
        try {
            Map<String, String> split = Splitter.on("&").withKeyValueSeparator("=").split(message);
            this.uuId = split.get("id");
            this.customerId = split.get("c");
            this.sessionId = split.get("s");
            this.eventName = split.get("e");
            this.url = split.get("u");
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public Event(String customerId, String sessionId, String eventName, String url) {
        this.customerId = customerId;
        this.sessionId = sessionId;
        this.eventName = eventName;
        this.url = url;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getUrl() {
        return url;
    }

    public String getCustomerId() {
        return this.customerId;
    }

    public String getUuId() {
        return this.uuId;
    }

    public void setUuId(String uuId) {
        this.uuId = uuId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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
        sb.append("c=").append(customerId);
        sb.append("&s=").append(sessionId);
        sb.append("&e=").append(eventName);
        sb.append("&u=").append(url);
        return sb.toString();
    }

    public Document getDbObject() {
        return new Document()
                .append("c", getCustomerId())
                .append("id", getUuId())
                .append("s", getSessionId())
                .append("e", getEventName())
                .append("u", getUrl());
    }
}
