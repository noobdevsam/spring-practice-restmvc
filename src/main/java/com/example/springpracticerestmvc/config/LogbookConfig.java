package com.example.springpracticerestmvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.Sink;
import org.zalando.logbook.json.JsonHttpLogFormatter;
import org.zalando.logbook.logstash.LogstashLogbackSink;

/**
 * Configuration class for Logbook, a library for HTTP request and response logging.
 */
@Configuration
public class LogbookConfig {

    /**
     * Creates a Logbook Sink that uses Logstash for logging.
     * The sink formats HTTP logs as JSON and sends them to Logstash.
     *
     * @return a configured LogstashLogbackSink instance.
     */
    @Bean
    public Sink logbookLogStash() {
        // Formatter for HTTP logs in JSON format
        HttpLogFormatter formatter = new JsonHttpLogFormatter();

        // Sink that sends formatted logs to Logstash
        return new LogstashLogbackSink(formatter);
    }
}