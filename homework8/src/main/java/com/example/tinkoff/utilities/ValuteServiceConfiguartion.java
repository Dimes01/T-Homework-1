package com.example.tinkoff.utilities;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ValuteServiceConfiguartion {
    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl("https://www.cbr.ru")
                .build();
    }
}
