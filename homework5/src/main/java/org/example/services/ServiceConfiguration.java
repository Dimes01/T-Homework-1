package org.example.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
class ServiceConfiguration {
    @Bean
    public RestClient getRestClient() {
        return RestClient.builder().baseUrl("https://kudago.com").build();
    }
}


