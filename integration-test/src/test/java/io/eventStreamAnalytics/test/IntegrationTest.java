package io.eventStreamAnalytics.test;

import io.EventStreamAnalytics.reporter.ReporterMain;
import io.eventStreamAnalytics.server.front.ServerMain;
import io.EventStreamAnalytics.test.event.EventGenerator;
import io.EventStreamAnalytics.test.event.EventGeneratorFactory;
import io.EventStreamAnalytics.test.event.EventGeneratorListenerImpl;
import io.eventStreamAnalytics.test.event.ReportingProcessor;
import io.eventStreamAnalytics.worker.WorkerMain;
import junit.framework.Assert;
import kafka.Kafka;
import org.apache.commons.io.FileUtils;
import org.apache.zookeeper.server.quorum.QuorumPeerMain;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by badal on 12/27/15.
 */
public class IntegrationTest {

    public static final ClassLoader SYSTEM_CLASS_LOADER = ClassLoader.getSystemClassLoader();
    private static Logger logger = LoggerFactory.getLogger(IntegrationTest.class);

    @Before
    public void setUp() throws IOException {
        cleanUp();
        startZookeeper();
        startKafka();
        startWorker();
        startFrontServer();
        startReporter();
    }

    private void cleanUp() throws IOException {
        File file = new File("target/tmp");
        logger.debug("Deleting Temp File from:" + file.getAbsolutePath());
        FileUtils.forceDelete(file);
    }

    @Test
    public void test() throws Exception {
        Thread.sleep(50000);
        try{
            EventGenerator clickEventGenerator = EventGeneratorFactory.getClickEventGenerator(
                    100, 10, new EventGeneratorListenerImpl());
            clickEventGenerator.generate();
        } catch (Exception ex) {
            logger.error("", ex);
        }

        Thread.sleep(50000);
        ReportingProcessor reportingProcessor = new ReportingProcessor();
        String value = reportingProcessor.processRequestAndGetBody("/user");
        Assert.assertEquals("I got a response: 1000",value);
    }

    private void startKafka() {
        startInNewThreadAndClassloader(() -> {
            String kafkaConfig = IntegrationTest.class.getClassLoader().getResource("server.properties").getPath();
            logger.debug("Starting Kafka server using config:" + kafkaConfig);
            Kafka.main(new String[]{kafkaConfig});
        }, "Kafka");
    }

    private void startZookeeper() {
        startInNewThreadAndClassloader(() -> {
            String zookeeperConfig = IntegrationTest.class.getClassLoader().getResource("zookeeper.properties").getPath();
            logger.debug("Starting Zookeeper server using config:" + zookeeperConfig);
            QuorumPeerMain.main(new String[]{zookeeperConfig});
        }, "Zookeeper");
    }

    private void startFrontServer() {
        startInNewThreadAndClassloader(() -> {
            ServerMain.main(new String[]{});
        }, "WorkerServer");
    }

    private void startWorker() {
        startInNewThreadAndClassloader(() -> {
            WorkerMain.main(new String[]{});
        }, "FrontServer");
    }

    private void startReporter() {
        startInNewThreadAndClassloader(() -> {
            ReporterMain.main(new String[]{});
        }, "Reporter");
    }

    private void startInNewThreadAndClassloader(Runnable runnable, String zookeeper) {
        Thread thread = new Thread(runnable);
        thread.setContextClassLoader(SYSTEM_CLASS_LOADER);
        thread.setName(zookeeper);
        thread.start();
    }
}
