package com.example.tinkoff.utilities;

import com.example.tinkoff.models.ValuteCurs;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class XmlMapperConfiguration {
    @Bean
    public static XmlMapper xmlMapper() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(ValuteCurs.class, new ValuteCursDeserializer());

        var xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JavaTimeModule());
        xmlMapper.registerModule(simpleModule);
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return xmlMapper;
    }
}
