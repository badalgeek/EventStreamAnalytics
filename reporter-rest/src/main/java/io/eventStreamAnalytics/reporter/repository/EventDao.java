package io.eventStreamAnalytics.reporter.repository;

import io.eventStreamAnalytics.model.TotalCustomer;

import java.util.List;

/**
 * Created by sandeep on 23/1/16.
 */
public interface EventDao {
    public List<TotalCustomer> getCustomerCount();
}
