package io.EventStreamAnalytics.test.event;

import io.eventStreamAnalytics.worker.model.Event;

/**
 * Created by badal on 1/9/16.
 */
public interface EventGeneratorListener {

    void onMessage(Event event);
}
