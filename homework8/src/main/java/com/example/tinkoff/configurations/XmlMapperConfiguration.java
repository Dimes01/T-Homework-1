package com.example.tinkoff.configurations;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XmlMapperConfiguration {
    @Bean
    public static XmlMapper standardXmlMapper() {
        return new XmlMapper();
    }

//    @Bean
//    public static XmlMapper xmlMapper() {
//        var xmlMapper = new XmlMapper();
//        xmlMapper.registerModule(new JavaTimeModule());
//        xmlMapper.registerModule(new Jdk8Module());
//        xmlMapper.registerModule(new ParameterNamesModule());
//        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        return xmlMapper;
//    }
}
