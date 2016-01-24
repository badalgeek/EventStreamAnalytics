package io.EventStreamAnalytics.test.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by badal on 1/9/16.
 */
public class EventGeneratorFactory {

    public static EventGenerator getClickEventGenerator(int noOfSession, int noOfPage,
                                                        EventGeneratorListener eventGeneratorListener) {
        List<String> pageList = getAutoList(noOfPage, "/page/page");
        ClickEventGenerator testCustomer = new ClickEventGenerator("TestCustomer",
                getSessionList(noOfSession), pageList);
        testCustomer.setEventGeneratorListener(eventGeneratorListener);
        return testCustomer;
    }

    public static EventGenerator getDeviceEventGenerator(int noOfSession, int noOfActivity, String deviceType,
                                                        EventGeneratorListener eventGeneratorListener) {
        List<String> activityList = getAutoList(noOfActivity, "activity");
        DeviceEventGenerator testCustomer = new DeviceEventGenerator("TestCustomer",
                getSessionList(noOfSession), activityList, deviceType);
        testCustomer.setEventGeneratorListener(eventGeneratorListener);
        return testCustomer;
    }

    private static List<String> getAutoList(int sizeOfList, String prefix) {
        List<String> activityList = new ArrayList<>();
        for (int i = 0; i < sizeOfList; i++) {
            activityList.add((prefix + i));
        }
        return activityList;
    }

    private static List<String> getSessionList(int noOfSession) {
        Random random = new Random();
        List<String> sessionList = new ArrayList<>();
        for (int i = 0; i < noOfSession; i++) {
            sessionList.add(Integer.toString(100000 + random.nextInt(100000)));
        }
        return sessionList;
    }
}
