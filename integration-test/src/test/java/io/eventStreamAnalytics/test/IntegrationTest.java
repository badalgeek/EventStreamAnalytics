package io.eventStreamAnalytics.test;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.model.*;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        startDynamoDb();
        createDynamoDbTable();
        startZookeeper();
        startKafka();
        startWorker();
        startFrontServer();
        startReporter();
    }

    private void cleanUp() throws IOException {
        File file = new File("target/tmp");
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

    private void startDynamoDb() {
        String property = System.getProperty("java.class.path");
        List<String> dylib = Arrays.asList(property.split(":")).stream().filter(s -> s.endsWith("dylib"))
                .collect(Collectors.toList());
        if(!dylib.isEmpty()) {
            System.load(dylib.get(0));
        }
        startInNewThreadAndClassloader(() -> {
            try {
                String[] localArgs = { "-sharedDb", "-inMemory" };
                ServerRunner.main(localArgs);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }, "DynamoDB");
    }

    private void startInNewThreadAndClassloader(Runnable runnable, String zookeeper) {
        Thread thread = new Thread(runnable);
        thread.setContextClassLoader(SYSTEM_CLASS_LOADER);
        thread.setName(zookeeper);
        thread.start();
    }

    private void createDynamoDbTable() throws InterruptedException {
        //Thread.sleep(40000);
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials("test", "test");
        AmazonDynamoDBClient client = new AmazonDynamoDBClient(basicAWSCredentials);
        client.setEndpoint("http://localhost:8000");
        DynamoDB dynamoDB = new DynamoDB(client);
        List<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();

        attributeDefinitions.add(new AttributeDefinition()
                .withAttributeName("c")
                .withAttributeType("S"));
        ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
        keySchema.add(new KeySchemaElement()
                .withAttributeName("c")
                .withKeyType(KeyType.HASH)); //Partition key

        attributeDefinitions.add(new AttributeDefinition()
                .withAttributeName("id")
                .withAttributeType("S"));
        keySchema.add(new KeySchemaElement()
                .withAttributeName("id")
                .withKeyType(KeyType.RANGE)); //Sort key

        String tableName = "events";
        CreateTableRequest request = new CreateTableRequest()
                .withTableName(tableName)
                .withKeySchema(keySchema)
                .withAttributeDefinitions(attributeDefinitions)
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits(5L)
                        .withWriteCapacityUnits(6L));

        logger.debug("Issuing CreateTable request for " + tableName);
        Table table = dynamoDB.createTable(request);

        logger.debug("Waiting for " + tableName
                + " to be created...this may take a while...");
        table.waitForActive();
    }

}
