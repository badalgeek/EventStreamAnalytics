package io.eventStreamAnalytics.worker.eventProcessor;

import akka.actor.UntypedActor;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import io.eventStreamAnalytics.model.Event;
import io.eventStreamAnalytics.worker.db.HazelCastFactory;
import scala.concurrent.Future;

import static akka.dispatch.Futures.future;

/**
 * Created by badal on 1/8/16.
 */
public class HazelcastEventActor extends UntypedActor {

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private IMap<String, Event> eventMap;

    @Override
    public void preStart() {
        HazelcastInstance hazelcastInstance = HazelCastFactory.getInstance();
        eventMap = hazelcastInstance.getMap("Events");
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            Future<Event> f = future(() -> {
                Event event = new Event((String) message);
                eventMap.put(event.getUuId(), event);
                return event;
            }, getContext().system().dispatcher());
            f.onSuccess(new OnSuccess<Event>() {
                @Override public final void onSuccess(Event t) {
                    log.debug("Processed String message: {}", t);
                }
            }, getContext().system().dispatcher());
        } else
            unhandled(message);
    }
}
