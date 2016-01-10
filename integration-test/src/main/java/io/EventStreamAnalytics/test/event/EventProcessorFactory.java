package io.EventStreamAnalytics.test.event;

import io.eventStreamAnalytics.test.event.EventProcessor;

/**
 * Created by sbadal on 1/10/16.
 */
public class EventProcessorFactory {

    private static EventProcessor instance;

    public synchronized static EventProcessor getInstance() {
        if(instance == null) {
            instance = new EventProcessor();
        }
        return instance;
    }
}
