package io.EventStreamAnalytics.test.event;

import io.eventStreamAnalytics.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by badal on 1/9/16.
 */
public class ClickEventGenerator implements EventGenerator {

    private EventGeneratorListener eventGeneratorListener;
    private String customerId;
    private String deviceId;
    private List<String> sessionIdList;
    private List<String> pageList;
    private List<MutableInteger> front;
    private Random random;
    private int size;

    public ClickEventGenerator(String customerId, List<String> sessionIdList, List<String> pageList, String deviceId) {
        this.customerId = customerId;
        this.sessionIdList = sessionIdList;
        this.pageList = pageList;
        this.size = sessionIdList.size();
        this.front = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            front.add(new MutableInteger(0));
        }
        this.random = new Random();
        this.deviceId = deviceId;
    }

    @Override
    public void generate() {
        while (!sessionIdList.isEmpty()) {
            int i = random.nextInt(size);
            String sessionId = sessionIdList.get(i);
            Event event = new Event(customerId, sessionId, "click", pageList.get(front.get(i).getValue()), deviceId );
            front.get(i).setValue(front.get(i).getValue() + 1);
            if ((front.get(i).getValue()) >= pageList.size()) {
                front.remove(i);
                sessionIdList.remove(i);
                size--;
            }
            eventGeneratorListener.onMessage(event);
        }
    }

    @Override
    public void setEventGeneratorListener(EventGeneratorListener eventGeneratorListener) {
        this.eventGeneratorListener = eventGeneratorListener;
    }

    public static class MutableInteger {

        private int value;

        public MutableInteger(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
