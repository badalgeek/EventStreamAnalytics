package io.eventStreamAnalytics.test.event;

import io.eventStreamAnalytics.model.Event;

/**
 * Created by badal on 1/9/16.
 */
public interface EventGeneratorListener {

    void onMessage(Event event);
}
