package com.example.tinkoff.configurations;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XmlMapperConfiguration {
    @Bean
    public static XmlMapper xmlMapper() {
        return new XmlMapper();
    }
}
