package io.eventStreamAnalytics.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.Storage;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import io.EventStreamAnalytics.test.event.EventGenerator;
import io.EventStreamAnalytics.test.event.EventGeneratorFactory;
import io.EventStreamAnalytics.test.event.EventGeneratorListenerImpl;
import io.eventStreamAnalytics.model.TotalCustomer;
import io.eventStreamAnalytics.model.utils.CommonUtil;
import io.eventStreamAnalytics.reporter.ReporterRestApp;
import io.eventStreamAnalytics.server.front.Server;
import io.eventStreamAnalytics.test.event.ReportingProcessor;
import io.eventStreamAnalytics.worker.WorkerMain;
import junit.framework.Assert;
import kafka.Kafka;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.zookeeper.server.quorum.QuorumPeerMain;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by badal on 12/27/15.
 */
public class IntegrationTest {

    static {
        cleanDirIfExist("target/logs");
    }

    public static final ClassLoader SYSTEM_CLASS_LOADER = ClassLoader.getSystemClassLoader();
    private static Logger logger = LoggerFactory.getLogger(IntegrationTest.class);

    @Before
    public void setUp() throws IOException, InterruptedException {
        //Arrays.asList(System.getProperty("java.class.path").split(":")).stream().forEach(s -> logger.info(s));
        cleanUp();
        startMangoDb();
        startReporter();
        startZookeeper();
        startKafka();
        startWorker();
        startFrontServer();
        Thread.sleep(30000);
    }

    private void cleanUp() throws IOException {
        cleanDirIfExist("target/tmp");
    }

    private static void cleanDirIfExist(String pathname) {
        File file = new File(pathname);
        if (file.exists()) {
            try {
                FileUtils.forceDelete(file);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete File:" + file.getAbsolutePath(), e);
            }
        }
    }

    @Test
    public void test() throws Exception {
        generateEvents();

        Thread.sleep(50000);
        ReportingProcessor reportingProcessor = new ReportingProcessor();
        String value = reportingProcessor.processRequestAndGetBody("/events/customers");
        List<TotalCustomer> actuallist = CommonUtil.fromJSON(new TypeReference<List<TotalCustomer>>() {
        }, value);
        String fixturePath = "io/eventStreamAnalytics/test/fixtures/DeviceList.json";
        String expectedJson = IOUtils.toString(IntegrationTest.class.getClassLoader().getResourceAsStream(fixturePath));
        List<TotalCustomer> expectedList = CommonUtil.fromJSON(new TypeReference<List<TotalCustomer>>() {
        }, expectedJson);

        Assert.assertEquals(expectedList.size(), actuallist.size());
        for (TotalCustomer expectedObject : expectedList) {
            Assert.assertTrue(actuallist.contains(expectedObject));
        }
    }

    private void generateEvents() {
        try {
            EventGenerator clickEventGenerator = EventGeneratorFactory.getClickEventGenerator(
                    100, 10, new EventGeneratorListenerImpl());
            EventGenerator androidEventGenerator = EventGeneratorFactory.getDeviceEventGenerator(
                    90, 19, "Android", new EventGeneratorListenerImpl());
            EventGenerator iPhoneEventGenerator = EventGeneratorFactory.getDeviceEventGenerator(
                    80, 15, "IOS", new EventGeneratorListenerImpl());
            EventGenerator windowsEventGenerator = EventGeneratorFactory.getDeviceEventGenerator(
                    70, 6, "Windows", new EventGeneratorListenerImpl());
            clickEventGenerator.generate();
            androidEventGenerator.generate();
            iPhoneEventGenerator.generate();
            windowsEventGenerator.generate();
        } catch (Exception ex) {
            logger.error("Failed to generate events:", ex);
            throw ex;
        }
    }

    private void startKafka() {
        startInNewThreadAndClassloader(() -> {
            try {
                String kafkaConfig = IntegrationTest.class.getClassLoader().getResource("server.properties").getPath();
                logger.debug("Starting Kafka server using config:" + kafkaConfig);
                Kafka.main(new String[]{kafkaConfig});
            } catch (RuntimeException ex) {
                logger.error("Failed to start kafka", ex);
                throw ex;
            }
        }, "Kafka");
    }

    private void startZookeeper() {
        startInNewThreadAndClassloader(() -> {
            try {
                String zookeeperConfig = IntegrationTest.class.getClassLoader().getResource("zookeeper.properties").getPath();
                logger.debug("Starting Zookeeper server using config:" + zookeeperConfig);
                QuorumPeerMain.main(new String[]{zookeeperConfig});
            } catch (RuntimeException ex) {
                logger.error("Failed to start zookeeper", ex);
                throw ex;
            }
        }, "Zookeeper");
    }

    private void startFrontServer() {
        startInNewThreadAndClassloader(() -> {
            try {
                String frontConfig = IntegrationTest.class.getClassLoader().getResource("test-application.conf").getPath();
                logger.debug("Starting Front server from config file:" + frontConfig);
                Server.main(new String[]{"-config", frontConfig});
            } catch (RuntimeException ex) {
                logger.error("Failed to start kafka", ex);
                throw ex;
            }
        }, "WorkerServer");
    }

    private void startWorker() {
        startInNewThreadAndClassloader(() -> {
            try {
                String reporterConfig = IntegrationTest.class.getClassLoader().getResource("test-application.conf").getPath();
                logger.debug("Starting Front server from config file:" + reporterConfig);
                WorkerMain.main(new String[]{"-config", reporterConfig});
            } catch (ParseException e) {
                logger.error("Failed to start worker", e);
                throw new RuntimeException(e);
            }
        }, "FrontServer");
    }

    private void startReporter() {
        startInNewThreadAndClassloader(() -> {
            try {
                ReporterRestApp.main(new String[]{});
            } catch (RuntimeException ex) {
                logger.error("Failed to start reporter", ex);
                throw ex;
            }
        }, "Reporter");
    }

    private void startMangoDb() throws InterruptedException {
        startInNewThreadAndClassloader(() -> {
            try {
                MongodStarter starter = MongodStarter.getDefaultInstance();
                IMongodConfig mongodConfig = new MongodConfigBuilder()
                        .version(Version.Main.PRODUCTION)
                        .net(new Net(12345, Network.localhostIsIPv6()))
                        .pidFile(new File("target/process.pid").getAbsolutePath())
                        .replication(new Storage(new File("target/tmp/mongodb/").getAbsolutePath(), null, 0))
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
                logger.error("Failed to start MongoDB", ex);
                throw new RuntimeException(ex);
            }
        }, "MangoDB").join();
        logger.debug("Successfully Started MongoDB.");
    }

    private Thread startInNewThreadAndClassloader(Runnable runnable, String zookeeper) {
        Thread thread = new Thread(runnable);
        thread.setName(zookeeper);
        thread.start();
        return thread;
    }

}
