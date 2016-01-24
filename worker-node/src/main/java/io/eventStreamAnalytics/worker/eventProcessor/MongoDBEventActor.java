package io.eventStreamAnalytics.worker.eventProcessor;

import akka.actor.UntypedActor;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.eventStreamAnalytics.model.Event;
import org.bson.Document;
import scala.concurrent.Future;

import static akka.dispatch.Futures.future;

/**
 * Created by badal on 1/8/16.
 */
public class MongoDBEventActor extends UntypedActor {

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private MongoCollection<Document> eventCollection;

    @Override
    public void preStart() {
        MongoClientURI uri  = new MongoClientURI("mongodb://localhost:12345/eventStreamAnalytics");
        MongoClient client = new MongoClient(uri);
        MongoDatabase mongoDatabase = client.getDatabase(uri.getDatabase());
        eventCollection = mongoDatabase.getCollection("events");
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            Future<Event> f = future(() -> {
                Event event = new Event((String) message);
                eventCollection.insertOne(event.getDbObject());
                return event;
            }, getContext().system().dispatcher());
            f.onSuccess(new OnSuccess<Event>() {
                @Override public final void onSuccess(Event t) {
                    log.debug("Processed String message: {}", message);
                }
            }, getContext().system().dispatcher());
        }
        else
            unhandled(message);
    }
}
