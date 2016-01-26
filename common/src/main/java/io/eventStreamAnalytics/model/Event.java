package io.eventStreamAnalytics.model;

import com.google.common.base.Splitter;
import org.bson.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    @Field(value = "d")
    private String deviceId;
    @Field(value = "da")
    private Long dateTimeinMillis;
    @Field(value = "day")
    private int day;
    @Field(value = "mo")
    private int month;
    @Field(value = "y")
    private int year;
    @Field(value = "ho")
    private int hour;

    private transient LocalDateTime dateTime;

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
            this.deviceId = split.get("d");
            this.dateTime = LocalDateTime.parse(split.get("date"),DateTimeFormatter.ofPattern("yyyy.MM.dd.HH:mm:ss:SSS"));
            this.dateTimeinMillis = this.dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            this.year = this.dateTime.getYear();
            this.month = this.dateTime.getMonthValue();
            this.day = this.dateTime.getDayOfMonth();
            this.hour = this.dateTime.getHour();

        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public Event(String customerId, String sessionId, String eventName, String url, String deviceId, LocalDateTime dateTime) {
        this.customerId = customerId;
        this.sessionId = sessionId;
        this.eventName = eventName;
        this.url = url;
        this.deviceId = deviceId;
        this.dateTime = dateTime;
        this.dateTimeinMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        this.year = dateTime.getYear();
        this.month = dateTime.getMonthValue();
        this.day = dateTime.getDayOfMonth();
        this.hour = dateTime.getHour();

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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }


    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public Long getDateTimeinMillis() {
        return dateTimeinMillis;
    }

    public void setDateTimeinMillis(Long dateTimeinMillis) {
        this.dateTimeinMillis = dateTimeinMillis;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append("/events?");
        sb.append("c=").append(customerId);
        sb.append("&s=").append(sessionId);
        sb.append("&e=").append(eventName);
        sb.append("&u=").append(url);
        sb.append("&d=").append(deviceId);
        sb.append("&udate=").append(dateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd.HH:mm:ss:SSS")));

        return sb.toString();
    }

    public Document getDbObject() {
        return new Document()
                .append("c", getCustomerId())
                .append("id", getUuId())
                .append("s", getSessionId())
                .append("e", getEventName())
                .append("d", getDeviceId())
                .append("u", getUrl())
                .append("da", getDateTimeinMillis())
                .append("y", getYear())
                .append("day", getDay())
                .append("mo", getMonth())
                .append("ho", getHour());
    }
}
