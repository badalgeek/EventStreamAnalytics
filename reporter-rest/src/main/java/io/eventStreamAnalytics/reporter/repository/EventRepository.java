package io.eventStreamAnalytics.reporter.repository;

import io.eventStreamAnalytics.model.Event;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by badal on 1/18/16.
 */
@EnableScan
@RepositoryRestResource
public interface EventRepository extends CrudRepository<Event, String> {

    Long countByCustomerId(@Param("status") String customerId);
}
