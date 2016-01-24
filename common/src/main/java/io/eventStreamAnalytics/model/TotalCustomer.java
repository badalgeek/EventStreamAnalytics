package io.eventStreamAnalytics.model;

/**
 * Created by sandeep on 23/1/16.
 */
public class TotalCustomer {

    private String deviceId;

    private long total;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
