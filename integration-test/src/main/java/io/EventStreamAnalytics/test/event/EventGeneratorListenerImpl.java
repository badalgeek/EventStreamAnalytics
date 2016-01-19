package io.EventStreamAnalytics.test.event;

import io.eventStreamAnalytics.test.event.EventProcessor;
import io.eventStreamAnalytics.model.Event;

/**
 * Created by badal on 1/9/16.
 */
public class EventGeneratorListenerImpl implements EventGeneratorListener {

    private EventProcessor eventProcessor = EventProcessorFactory.getInstance();

    @Override
    public void onMessage(Event event) {
        eventProcessor.processRequest(event.deSerialize());
    }
}
