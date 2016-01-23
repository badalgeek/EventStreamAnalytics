package io.eventStreamAnalytics.test;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import io.EventStreamAnalytics.test.event.EventGenerator;
import io.EventStreamAnalytics.test.event.EventGeneratorFactory;
import io.EventStreamAnalytics.test.event.EventGeneratorListenerImpl;
import io.eventStreamAnalytics.server.front.ServerMain;
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
import io.eventStreamAnalytics.reporter.ReporterRestApp;

import java.io.File;
import java.io.IOException;

/**
 * Created by badal on 12/27/15.
 */
public class IntegrationTest {

    public static final ClassLoader SYSTEM_CLASS_LOADER = ClassLoader.getSystemClassLoader();
    private static Logger logger = LoggerFactory.getLogger(IntegrationTest.class);

    @Before
    public void setUp() throws IOException, InterruptedException {
        //Arrays.asList(System.getProperty("java.class.path").split(":")).stream().forEach(s -> System.out.println(s));
        cleanUp();
        startMangoDb();
        startReporter();
        startZookeeper();
        startKafka();
        startWorker();
        startFrontServer();
    }

    private void cleanUp() throws IOException {
        cleanDirIfExist("target/tmp");
        cleanDirIfExist("target/logs");
    }

    private void cleanDirIfExist(String pathname) throws IOException {
        File file = new File(pathname);
        if (file.exists()) {
            logger.debug("Deleting Temp File from:" + file.getAbsolutePath());
            FileUtils.forceDelete(file);
        }
    }

    @Test
    public void test() throws Exception {
        Thread.sleep(30000);
        try {
            EventGenerator clickEventGenerator = EventGeneratorFactory.getClickEventGenerator(
                    100, 10, new EventGeneratorListenerImpl());
            clickEventGenerator.generate();
        } catch (Exception ex) {
            logger.error("Failed to generate ", ex);
        }

        Thread.sleep(50000);
        ReportingProcessor reportingProcessor = new ReportingProcessor();
        String value = reportingProcessor.processRequestAndGetBody("/events/count");
        Assert.assertEquals("1000", value);
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
            ReporterRestApp.main(new String[]{});
        }, "Reporter");
    }

    private void startMangoDb() throws InterruptedException {
        startInNewThreadAndClassloader(() -> {
            try {
                MongodStarter starter = MongodStarter.getDefaultInstance();
                IMongodConfig mongodConfig = new MongodConfigBuilder()
                        .version(Version.Main.PRODUCTION)
                        .net(new Net(12345, Network.localhostIsIPv6()))
                        .build();
                logger.debug("Would download MongoDB if not yet downloaded.");
                MongodExecutable mongodExecutable = starter.prepare(mongodConfig);
                logger.debug("Done with downloading MongoDB exec.");
                mongodExecutable.start();

                MongoClientURI uri = new MongoClientURI("mongodb://localhost:12345/eventStreamAnalytics");
                MongoClient client = new MongoClient(uri);
                MongoDatabase mongoDatabase = client.getDatabase(uri.getDatabase());
                mongoDatabase.createCollection("events");
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }, "MangoDB").join();
    }

    private Thread startInNewThreadAndClassloader(Runnable runnable, String zookeeper) {
        Thread thread = new Thread(runnable);
        thread.setContextClassLoader(SYSTEM_CLASS_LOADER);
        thread.setName(zookeeper);
        thread.start();
        return thread;
    }

}
