package io.eventStreamAnalytics.worker;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.eventStreamAnalytics.worker.db.HazelCastFactory;
import io.eventStreamAnalytics.worker.eventConsumer.KafkaEventConsumer;
import org.apache.commons.cli.*;

import java.io.File;

/**
 * Created by badal on 1/8/16.
 */
public class WorkerMain {

    public static final String CONFIG = "config";
    private static final String EMBEDDED = "embedded";
    private CommandLine cmd;

    public WorkerMain(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption(CONFIG, true, "Config File Full Path");
        options.addOption(EMBEDDED, false, "If Worker running as embedded");
        CommandLineParser parser = new DefaultParser();
        cmd = parser.parse( options, args);
    }

    public void start() {
        Config rootConfig;
        if(cmd.hasOption(CONFIG)) {
            rootConfig = ConfigFactory.parseFile(new File(cmd.getOptionValue(CONFIG)));
        } else {
            rootConfig = ConfigFactory.load("eventStreamWorker.conf");
        }
        HazelCastFactory.getInstance();
        Config eventStreamAnalyticsFront = rootConfig.getConfig("EventStreamAnalyticsWorker");
        ActorSystem system = ActorSystem.create("EventStreamAnalyticsWorker", eventStreamAnalyticsFront);
        KafkaEventConsumer consumer = new KafkaEventConsumer(system);
        if(cmd.hasOption(EMBEDDED)) {
            new Thread(consumer).start();
        } else {
            consumer.run();
        }
    }

    public static void main(String[] args) throws ParseException {
        new WorkerMain(args).start();
    }
}
