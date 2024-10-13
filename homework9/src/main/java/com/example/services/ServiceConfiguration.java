package com.example.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
class ServiceConfiguration {
    @Value("${spring.datasource.url}")
    private String urlKudaGoService;

    @Value("${services.currency-service.host}")
    private String urlCurrencyService;

    @Bean("kudago-service")
    public RestClient getRestClientKudaGo() {
        return RestClient.builder().baseUrl(urlKudaGoService).build();
    }

    @Bean("currency-service")
    public RestClient getRestClientCurrencyService() {
        return RestClient.builder().baseUrl(urlCurrencyService).build();
    }
}
