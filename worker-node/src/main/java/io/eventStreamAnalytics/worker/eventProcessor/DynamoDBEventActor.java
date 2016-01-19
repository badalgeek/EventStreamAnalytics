package io.eventStreamAnalytics.worker.eventProcessor;

import akka.actor.UntypedActor;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.concurrent.Future;
import static akka.dispatch.Futures.future;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import io.eventStreamAnalytics.model.Event;

/**
 * Created by badal on 1/8/16.
 */
public class DynamoDBEventActor extends UntypedActor {

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private Table eventsTable;

    @Override
    public void preStart() {
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials("test", "test");
        AmazonDynamoDBClient client = new AmazonDynamoDBClient(basicAWSCredentials);
        client.setEndpoint("http://localhost:8000");
        DynamoDB dynamoDB = new DynamoDB(client);
        eventsTable = dynamoDB.getTable("events");
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            Future<Event> f = future(() -> {
                Event event = new Event((String) message);
                eventsTable.putItem(event.getItem());
                return event;
            }, getContext().system().dispatcher());
            f.onSuccess(new OnSuccess<Event>() {
                @Override public final void onSuccess(Event t) {
                    log.debug("Processed String message: {}", t);
                }
            }, getContext().system().dispatcher());
            log.info("Processed String message: {}", message);
        }
        else
            unhandled(message);
    }
}
