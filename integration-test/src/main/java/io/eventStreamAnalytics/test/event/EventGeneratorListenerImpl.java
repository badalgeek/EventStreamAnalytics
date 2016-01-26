package io.eventStreamAnalytics.test.event;

import io.eventStreamAnalytics.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by badal on 1/9/16.
 */
public class EventGeneratorListenerImpl implements EventGeneratorListener {

    private static Logger logger = LoggerFactory.getLogger(EventGeneratorListenerImpl.class);
    private EventProcessor eventProcessor = EventProcessorFactory.getInstance();

    @Override
    public void onMessage(Event event) {
        String eventString = event.serialize();
        logger.debug("Event Generated:" + eventString);
        eventProcessor.processRequest(eventString);
    }
}
