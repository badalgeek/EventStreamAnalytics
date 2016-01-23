package io.eventStreamAnalytics.worker.eventConsumer;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.eventStreamAnalytics.worker.eventProcessor.HazelcastEventActor;
import io.eventStreamAnalytics.worker.eventProcessor.MongoDBEventActor;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import kafka.serializer.StringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by badal on 1/8/16.
 */
public class KafkaEventConsumer {

    private ConsumerConnector consumerConnector;
    private ActorRef hazelcastEventActor;
    private ActorRef mangoDBEventActor;
    private static Logger logger = LoggerFactory.getLogger(KafkaEventConsumer.class);

    public KafkaEventConsumer() {
        logger.debug("Initilizing Consumer");
        Config config = ConfigFactory.load();
        Config eventStreamAnalyticsFront = config.getConfig("EventStreamAnalyticsWorker");
        ActorSystem system = ActorSystem.create("EventStreamAnalyticsWorker", eventStreamAnalyticsFront);
        hazelcastEventActor = system.actorOf(Props.create(HazelcastEventActor.class), "hazelcastEventHandler");
        mangoDBEventActor = system.actorOf(Props.create(MongoDBEventActor.class), "mongoDbEventHandler");
        Properties props = new Properties();
        props.put("zookeeper.connect", "localhost:2181");
        props.put("group.id", "eventProcessor");
        props.put("client.id", "workerEventProcessor");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", "smallest");
        ConsumerConfig consumerConfig = new ConsumerConfig(props);
        consumerConnector = Consumer.createJavaConsumerConnector(consumerConfig);
        logger.debug("Intialized Consumer");
    }

    public void start() {
        Map<String, List<KafkaStream<String, String>>> topicMessageStreams =
                consumerConnector.createMessageStreams(ImmutableMap.of("events", 1),
                        new StringDecoder(null), new StringDecoder(null));

        List<KafkaStream<String, String>> events = topicMessageStreams.get("events");
        if (events.size() != 1) {
            throw new RuntimeException("Unexpected Result. Closing the server");
        }
        KafkaStream<String, String> stream = events.get(0);
        for (MessageAndMetadata<String, String> next : stream) {
            hazelcastEventActor.tell(next.message(), null);
            mangoDBEventActor.tell(next.message(), null);
        }
    }

}
