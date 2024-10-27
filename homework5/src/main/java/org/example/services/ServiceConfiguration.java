package org.example.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.util.concurrent.Semaphore;

@Configuration
class ServiceConfiguration {
    @Value("${services.kudago-service.max-concurrent-requests}")
    private int maxConcurrentRequests;

    @Bean
    public RestClient getRestClient() {
        return RestClient.builder().baseUrl("https://kudago.com").build();
    }

    @Bean("semaphore")
    public Semaphore getSemaphore() {
        return new Semaphore(maxConcurrentRequests);
    }
}


