package io.eventStreamAnalytics.worker.model;

import com.google.common.base.Splitter;
import joptsimple.internal.Strings;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by badal on 1/9/16.
 */
public class Event  implements Serializable {

    private String customerId;
    private String sessionId;
    private String uuId;
    private String eventName;
    private String url;

    public Event(String message) {
        try {
            Map<String, String> split = Splitter.on("&").withKeyValueSeparator("=").split(message);
            this.customerId = split.get("c");
            this.sessionId = split.get("s");
            this.uuId = split.get("id");
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

    public String getKey() {
        return Strings.join(new String[]{customerId, sessionId, uuId}, ":");
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUuId() {
        return uuId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getUrl() {
        return url;
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
}
