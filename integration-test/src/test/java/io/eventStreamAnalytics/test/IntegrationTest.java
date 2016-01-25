package io.eventStreamAnalytics.test;

import com.fasterxml.jackson.core.type.TypeReference;
import io.eventStreamAnalytics.test.event.EventGenerator;
import io.eventStreamAnalytics.test.event.EventGeneratorFactory;
import io.eventStreamAnalytics.test.event.EventGeneratorListenerImpl;
import io.eventStreamAnalytics.model.TotalCustomer;
import io.eventStreamAnalytics.model.utils.CommonUtil;
import io.eventStreamAnalytics.test.event.ReportingProcessor;
import io.eventStreamAnalytics.test.testServer.TestServerManager;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by badal on 12/27/15.
 */
public class IntegrationTest {

    private static Logger logger = LoggerFactory.getLogger(IntegrationTest.class);

    @Before
    public void setUp() throws IOException, InterruptedException {
        new TestServerManager().startTestServer();
    }

    @Test
    public void test() throws Exception {
        generateEvents();

        Thread.sleep(10000);
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
}
