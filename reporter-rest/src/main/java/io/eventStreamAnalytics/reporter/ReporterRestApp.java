package io.eventStreamAnalytics.reporter;

import io.eventStreamAnalytics.reporter.config.SpringDataDynamoConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created by badal on 1/15/16.
 */
@Configuration
@ComponentScan(basePackages = "io.eventStreamAnalytics.reporter")
@EnableAutoConfiguration
@EnableEntityLinks
@EnableAsync
@Import(value = {SpringDataDynamoConfig.class})
public class ReporterRestApp {

    private static Logger logger = LoggerFactory.getLogger(ReporterRestApp.class);

    public static void main(String[] args) {
        logger.info("Starting reporter");
        try{
            SpringApplication.run(ReporterRestApp.class, args);
        } catch (RuntimeException ex) {
            logger.error("Error in booting reporter", ex);
            throw ex;
        }
    }
}