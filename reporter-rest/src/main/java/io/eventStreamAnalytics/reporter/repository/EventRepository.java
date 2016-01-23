package io.eventStreamAnalytics.reporter.repository;

import io.eventStreamAnalytics.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * Created by badal on 1/18/16.
 */
@RepositoryRestResource(collectionResourceRel = "events", path = "events")
public interface EventRepository extends MongoRepository<Event, String> {

    @RestResource(rel = "totalEventCount")
    Long countByCustomerId(@Param("customerId") String customerId);
}
