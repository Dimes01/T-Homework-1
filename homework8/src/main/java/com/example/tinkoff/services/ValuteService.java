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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.jackson.date-format}")
    private String dateFormat;

    private final Logger logger = LoggerFactory.getLogger(ValuteService.class);

    @Cacheable(value = "currenciesCursesByDate", key = "#date")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "circuitFallbackCurrenciesCursesByDate")
    public ValuteCurs getCurrenciesCursesByDate(LocalDate date) throws JsonProcessingException {
        logger.info("Method 'getCurrenciesCursesByDate': started");
        var dateFormatted = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        var response = restClient.get()
                .uri("/scripts/XML_daily.asp?date_req=" + dateFormatted)
                .retrieve()
                .body(String.class);
        logger.debug("Method 'getCurrenciesCursesByDate': currencies curses is received");
        ValuteCurs valCurs = xmlMapper.readValue(response, ValuteCurs.class);
        if (valCurs != null) {
            logger.debug("Method 'getCurrenciesCursesByDate': currencies curses is converted");
        }
        logger.info("Method 'getCurrenciesCursesByDate': finished");
        return valCurs;
    }

    public double calculateAmountBetweenCurrencies(Valute currencyFrom, double amount, Valute currencyTo) {
        double currencyToValue = Double.parseDouble(currencyTo.getValue());
        double currencyFromValue = Double.parseDouble(currencyFrom.getValue());
        return currencyToValue / (currencyFromValue * amount);
    }

    public ValuteCurs circuitFallbackCurrenciesCursesByDate(Throwable throwable) {
        logger.error("Circuit breaker activated for 'getCurrenciesCursesByDate': {}", throwable.getMessage());
        return null;
    }
}
