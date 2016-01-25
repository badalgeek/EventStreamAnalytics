package io.eventStreamAnalytics.test.event;

import java.util.List;

/**
 * Created by sandeep on 24/1/16.
 */
public class DeviceEventGenerator extends AbstractEventGenerator implements EventGenerator {

    public DeviceEventGenerator(String customerId, List<String> sessionIdList, List<String> activityList, String deviceId) {
        super(customerId, sessionIdList, activityList, deviceId);
    }

}
