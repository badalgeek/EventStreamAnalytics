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
            //Config config = new XmlConfigBuilder().build();
            instance = Hazelcast.newHazelcastInstance();
//            final AtomicBoolean isStarted = new AtomicBoolean();
//            instance.getLifecycleService().addLifecycleListener(new LifecycleListener() {
//                @Override
//                public void stateChanged(LifecycleEvent event) {
//                    if(event.getState() == LifecycleEvent.LifecycleState.STARTED){
//                        isStarted.set(true);
//                    }
//                }
//            });
//            while(!isStarted.get()) {
//                try {
//                    Thread.sleep(50);
//                } catch (InterruptedException ex) {
//                    throw new RuntimeException(ex);
//                }
//            }
        }
        return instance;
    }
}
