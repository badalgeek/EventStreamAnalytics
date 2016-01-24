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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TotalCustomer that = (TotalCustomer) o;

        if (total != that.total) return false;
        return deviceId != null ? deviceId.equals(that.deviceId) : that.deviceId == null;

    }

    @Override
    public int hashCode() {
        int result = deviceId != null ? deviceId.hashCode() : 0;
        result = 31 * result + (int) (total ^ (total >>> 32));
        return result;
    }
}
