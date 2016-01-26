package io.eventStreamAnalytics.dto;

/**
 * Created by sandeep on 23/1/16.
 */
public class UniqueCustomerCountByDeviceType {

    private String deviceType;

    private long total;

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
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

        UniqueCustomerCountByDeviceType that = (UniqueCustomerCountByDeviceType) o;

        if (total != that.total) return false;
        return deviceType != null ? deviceType.equals(that.deviceType) : that.deviceType == null;

    }

    @Override
    public int hashCode() {
        int result = deviceType != null ? deviceType.hashCode() : 0;
        result = 31 * result + (int) (total ^ (total >>> 32));
        return result;
    }
}
