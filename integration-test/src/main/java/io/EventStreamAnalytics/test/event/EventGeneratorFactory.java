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
        List<String> pageList = getPageList(noOfPage, "/page/page");
        ClickEventGenerator testCustomer = new ClickEventGenerator("TestCustomer", getSessionList(noOfSession), pageList);
        testCustomer.setEventGeneratorListener(eventGeneratorListener);
        return testCustomer;
    }

    private static List<String> getPageList(int noOfPage, String page) {
        List<String> pageList = new ArrayList<>();
        for (int i = 0; i < noOfPage; i++) {
            pageList.add(page + i);
        }
        return pageList;
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
