package io.EventStreamAnalytics.test.event;

import io.eventStreamAnalytics.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by sandeep on 24/1/16.
 */
public class AbstractEventGenerator implements EventGenerator {

    public EventGeneratorListener eventGeneratorListener;
    public String customerId;
    public String deviceId;
    public List<String> sessionIdList;
    public List<String> flowList;
    public List<MutableInteger> front;
    public Random random;
    public int size;

    public AbstractEventGenerator(String customerId, List<String> sessionIdList, List<String> flowList, String deviceId) {
        this.customerId = customerId;
        this.sessionIdList = sessionIdList;
        this.flowList = flowList;
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
            Event event = new Event(customerId, sessionId, "click", flowList.get(front.get(i).getValue()), deviceId );
            front.get(i).setValue(front.get(i).getValue() + 1);
            if ((front.get(i).getValue()) >= flowList.size()) {
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
