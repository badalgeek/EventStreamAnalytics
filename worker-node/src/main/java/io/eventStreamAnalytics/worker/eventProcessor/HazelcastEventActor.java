package io.eventStreamAnalytics.worker.eventProcessor;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import io.eventStreamAnalytics.worker.db.HazelCastFactory;
import io.eventStreamAnalytics.model.Event;

/**
 * Created by badal on 1/8/16.
 */
public class HazelcastEventActor extends UntypedActor {

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private IMap<String, Event> eventMap;

    @Override
    public void preStart() {
        HazelcastInstance hazelcastInstance =  HazelCastFactory.getInstance();
        eventMap = hazelcastInstance.getMap("Events");
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            //@TODO Wrap in future
            Event event = new Event((String) message);
            eventMap.put(event.getUuId(), event);
            log.info("Processed String message: {}", message);
        }
        else
            unhandled(message);
    }
}
