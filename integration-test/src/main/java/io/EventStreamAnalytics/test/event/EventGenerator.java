package io.EventStreamAnalytics.test.event;

/**
 * Created by sbadal on 1/9/16.
 */
public interface EventGenerator {

    void generate();

    void setEventGeneratorListener(EventGeneratorListener eventGeneratorListener);
}
