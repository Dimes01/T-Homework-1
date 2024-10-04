package com.example.tinkoff.services;

import com.example.tinkoff.models.ValuteCurs;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class ValuteService {
    @Autowired
    private RestClient restClient;
    @Autowired
    private XmlMapper xmlMapper;

    private final Logger logger = LoggerFactory.getLogger(ValuteService.class);

    public ValuteCurs getCurrenciesCursesByDate(LocalDate date) throws JsonProcessingException {
        logger.info("Method 'getCurrenciesCursesByDate': started");
        var response = restClient.get()
                .uri("/scripts/XML_daily.asp?date_req=" + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .retrieve()
                .body(String.class);
        logger.debug("Method 'getCurrenciesCursesByDate': currencies curses is received");
        ValuteCurs valCurs;
        try {
            valCurs = xmlMapper.readValue(response, ValuteCurs.class);
            logger.debug("Method 'getCurrenciesCursesByDate': currencies curses is converted");
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            throw e;
        }
        logger.info("Method 'getCurrenciesCursesByDate': finished");
        return valCurs;
    }
}
