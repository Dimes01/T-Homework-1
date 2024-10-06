package com.example.tinkoff.services;

import com.example.tinkoff.models.AllValutes;
import com.example.tinkoff.models.ValuteCurs;
import com.example.tinkoff.models.ValuteInfo;
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
import java.util.Objects;

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
        ValuteCurs valCurs = null;
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

    public ValuteInfo getValuteInfoByISOCharCode(String isoCharCode) throws JsonProcessingException {
        logger.info("Method 'getValuteInfoByISOCharCode': started");
        var response = restClient.get()
                .uri("/scripts/XML_valFull.asp")
                .retrieve()
                .body(String.class);
        ValuteInfo valuteInfo = null;
        try {
            valuteInfo = xmlMapper.readValue(response, AllValutes.class)
                    .getValutes().stream()
                    .filter(v -> Objects.equals(v.getIsoCharCode(), isoCharCode))
                    .findFirst()
                    .orElse(null);
            logger.debug("Method 'getValuteInfoByISOCharCode': currencies curses is converted");
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            throw e;
        }
        logger.info("Method 'getValuteInfoByISOCharCode': finished");
        return valuteInfo;
    }
}
