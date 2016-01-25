package io.eventStreamAnalytics.worker.db;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by badal on 1/9/16.
 */
public class HazelCastFactory {

    public static HazelcastInstance instance;

    public synchronized static HazelcastInstance getInstance() {
        if (instance == null) {
            instance = Hazelcast.newHazelcastInstance();
        }
        return instance;
    }
}
