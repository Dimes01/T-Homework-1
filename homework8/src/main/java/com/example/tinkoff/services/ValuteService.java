package com.example.tinkoff.services;

import com.example.tinkoff.models.AllValutes;
import com.example.tinkoff.models.Valute;
import com.example.tinkoff.models.ValuteCurs;
import com.example.tinkoff.models.ValuteInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = "currenciesCursesByDate", key = "#date")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "circuitFallbackCurrenciesCursesByDate")
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

    @Cacheable(value = "valuteInfoByISOCharCode", key = "#isoCharCode")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "circuitFallbackValuteInfoByISOCharCode")
    public ValuteInfo getValuteInfoByISOCharCode(String isoCharCode) throws JsonProcessingException {
        logger.info("Method 'getValuteInfoByISOCharCode': started");
        var response = restClient.get()
                .uri("/scripts/XML_valFull.asp")
                .retrieve()
                .body(String.class);
        ValuteInfo valuteInfo;
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

    public double calculateAmountBetweenCurrencies(Valute currencyFrom, double amount, Valute currencyTo) {
        return currencyTo.getValue() / (currencyFrom.getValue() * amount);
    }

    public ValuteCurs circuitFallbackCurrenciesCursesByDate(Throwable throwable) {
        logger.error("Circuit breaker activated for 'getCurrenciesCursesByDate': {}", throwable.getMessage());
        return null;
    }

    public ValuteInfo circuitFallbackValuteInfoByISOCharCode(String isoCharCode, Throwable throwable) {
        logger.error("Circuit breaker activated for 'getValuteInfoByISOCharCode': {}", throwable.getMessage());
        return null;
    }

}
