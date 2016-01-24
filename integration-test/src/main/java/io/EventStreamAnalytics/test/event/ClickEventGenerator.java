package io.EventStreamAnalytics.test.event;

import java.util.List;

/**
 * Created by badal on 1/9/16.
 */
public class ClickEventGenerator extends AbstractEventGenerator implements EventGenerator {

    public ClickEventGenerator(String customerId, List<String> sessionIdList, List<String> pageList) {
        super(customerId, sessionIdList, pageList, "Browser");
    }

}
