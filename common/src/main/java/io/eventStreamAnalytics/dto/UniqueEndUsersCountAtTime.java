package io.eventStreamAnalytics.dto;

/**
 * Created by shouvik on 26/01/16.
 */
public class UniqueEndUsersCountAtTime {

    private int count;

    private long time;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
