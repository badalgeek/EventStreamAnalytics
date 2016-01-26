package io.eventStreamAnalytics.reporter.repository;

import io.eventStreamAnalytics.dto.UniqueCustomerCountByDeviceType;
import io.eventStreamAnalytics.dto.UniqueEndUsersCountAtTime;

import java.util.List;

/**
 * Created by sandeep on 23/1/16.
 */
public interface EventDao {
    public List<UniqueCustomerCountByDeviceType> getCustomerCount();

    public List<UniqueEndUsersCountAtTime> getUniqueEndUsersCountByTime();
}
