package io.eventStreamAnalytics.test.testServer;

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
import io.eventStreamAnalytics.reporter.ReporterRestApp;
import io.eventStreamAnalytics.server.front.Server;
import io.eventStreamAnalytics.test.event.EventProcessor;
import io.eventStreamAnalytics.test.event.HttpProcessor;
import io.eventStreamAnalytics.test.event.ReportingProcessor;
import io.eventStreamAnalytics.worker.WorkerMain;
import kafka.Kafka;
import kafka.server.KafkaServerStartable;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.zookeeper.server.quorum.QuorumPeerMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by badal on 1/25/16.
 */
public class TestServerManager {

    static {
        cleanDirIfExist("target/logs");
    }

    public static final int WAIT_BETWEEN_PING = 100;
    private static Logger logger = LoggerFactory.getLogger(TestServerManager.class);
    private static boolean isStarted = false;

    public synchronized void startTestServer() throws InterruptedException, IOException {
        if(!isStarted) {
            cleanUp();
            startMangoDb();
            startZookeeper();
            startKafka();
            startWorker();
            startReporter();
            startFrontServer();
            isStarted = true;
        }
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

    private void startKafka() throws InterruptedException {
        startInNewThread(() -> {
            try {
                String kafkaConfig = TestServerManager.class.getClassLoader().getResource("server.properties").getPath();
                logger.debug("Starting Kafka server using config:" + kafkaConfig);
                String[] kafkaArgs = {kafkaConfig};
                Properties serverProps = Kafka.getPropsFromArgs(kafkaArgs);
                KafkaServerStartable kafkaServerStartable = KafkaServerStartable.fromProps(serverProps);
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        kafkaServerStartable.shutdown();
                    }
                });
                kafkaServerStartable.startup();
            } catch (RuntimeException ex) {
                logger.error("Failed to start kafka", ex);
                throw ex;
            }
        }, "Kafka").join();
        logger.debug("Kafka started.");
    }

    private void startZookeeper() {
        startInNewThread(() -> {
            try {
                String zookeeperConfig = TestServerManager.class.getClassLoader().getResource("zookeeper.properties").getPath();
                logger.debug("Starting Zookeeper server using config:" + zookeeperConfig);
                QuorumPeerMain.main(new String[]{zookeeperConfig});
            } catch (RuntimeException ex) {
                logger.error("Failed to start zookeeper", ex);
                throw ex;
            }
        }, "Zookeeper");
        logger.debug("Waiting until zookeper is started");
        new ZkClient("localhost:2181").waitUntilConnected();
        logger.debug("Zookeeper started");
    }

    private void startFrontServer() throws InterruptedException {
        startInNewThread(() -> {
            try {
                String frontConfig = TestServerManager.class.getClassLoader().getResource("test-application.conf").getPath();
                logger.debug("Starting Front server from config file:" + frontConfig);
                Server.main(new String[]{"-config", frontConfig});
            } catch (RuntimeException ex) {
                logger.error("Failed to start kafka", ex);
                throw ex;
            }
        }, "WorkerServer");
        logger.debug("Waiting for front server to started");
        waitTillPingRespond(new EventProcessor());
        logger.debug("Front server started");
    }

    private void waitTillPingRespond(HttpProcessor httpProcessor) throws InterruptedException {
        while (true) {
            try {
                if (httpProcessor.processRequestAndGetBody("/ping").equals("Hola")) {
                    break;
                }
            } catch (Exception ex) {
                logger.debug("Front Server is not yet started");
            }
            Thread.sleep(WAIT_BETWEEN_PING);
        }
    }

    private void startWorker() throws InterruptedException {
        startInNewThread(() -> {
            try {
                String reporterConfig = TestServerManager.class.getClassLoader().getResource("test-application.conf").getPath();
                logger.debug("Starting Front server from config file:" + reporterConfig);
                WorkerMain.main(new String[]{"-config", reporterConfig, "-embedded"});
            } catch (ParseException e) {
                logger.error("Failed to start worker", e);
                throw new RuntimeException(e);
            }
        }, "FrontServer").join();
        logger.debug("Worker Node Started");
    }

    private void startReporter() throws InterruptedException {
        startInNewThread(() -> {
            try {
                ReporterRestApp.main(new String[]{});
            } catch (RuntimeException ex) {
                logger.error("Failed to start reporter", ex);
                throw ex;
            }
        }, "Reporter");
        logger.debug("Waiting for reporter to started");
        waitTillPingRespond(new ReportingProcessor());
        logger.debug("Reporter started");
    }

    private void startMangoDb() throws InterruptedException {
        startInNewThread(() -> {
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

    private Thread startInNewThread(Runnable runnable, String zookeeper) {
        Thread thread = new Thread(runnable);
        thread.setName(zookeeper);
        thread.start();
        return thread;
    }
}
