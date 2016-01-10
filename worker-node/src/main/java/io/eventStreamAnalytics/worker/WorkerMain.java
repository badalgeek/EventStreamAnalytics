package io.eventStreamAnalytics.worker;

import io.eventStreamAnalytics.worker.db.HazelCastFactory;
import io.eventStreamAnalytics.worker.eventConsumer.KafkaEventConsumer;

/**
 * Created by badal on 1/8/16.
 */
public class WorkerMain {

    public static void main(String[] args){
        HazelCastFactory.getInstance();
        KafkaEventConsumer consumer = new KafkaEventConsumer();
        consumer.start();
    }
}
