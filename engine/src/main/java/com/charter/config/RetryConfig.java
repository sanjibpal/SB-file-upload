package com.charter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RetryConfig {
    @Value("${app.file.download.attempts}")
    private int NUM_ATTEMPTS;

    /**
     * Configured to retry downloading the file up to 10 times with exponential backoff.
     * @return retrytemplate
     */
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>() {{
            put(Exception.class, true);
        }};
        RetryPolicy retryPolicy = new SimpleRetryPolicy(NUM_ATTEMPTS, retryableExceptions);

        BackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        return retryTemplate;
    }
}
