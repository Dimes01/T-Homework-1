package com.example.tinkoff.services;

import com.example.tinkoff.models.AllValutes;
import com.example.tinkoff.models.Valute;
import com.example.tinkoff.models.ValuteCurs;
import com.example.tinkoff.models.ValuteInfo;
import com.example.tinkoff.utilities.CurrencyNotExistException;
import com.example.tinkoff.utilities.CurrencyNotFoundException;
import com.example.tinkoff.utilities.ServiceUnavailableException;
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

    private final String dateFormat = "dd/MM/yyyy";

    private final Logger logger = LoggerFactory.getLogger(ValuteService.class);

    @Cacheable(value = "currenciesCursesByDate")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "circuitFallbackCurrencyCursByDate")
    public Valute getCurrencyCursByDate(LocalDate date, String isoCharCode) throws JsonProcessingException, CurrencyNotFoundException {
        logger.info("Method 'getCurrenciesCursesByDate': started");

        var dateFormatted = date.format(DateTimeFormatter.ofPattern(dateFormat));
        var response = restClient.get()
                .uri("/scripts/XML_daily.asp?date_req=" + dateFormatted)
                .retrieve()
                .body(String.class);
        logger.debug("Method 'getCurrenciesCursesByDate': currencies curses is received");

        Valute currency = xmlMapper.readValue(response, ValuteCurs.class)
                .getValutes().stream()
                .filter(c -> Objects.equals(c.getCharCode(), isoCharCode))
                .findFirst().orElse(null);
        if (currency == null) throw new CurrencyNotFoundException(isoCharCode);
        logger.debug("Method 'getCurrenciesCursesByDate': currencies curses is converted");

        logger.info("Method 'getCurrenciesCursesByDate': finished");
        return currency;
    }

    @Cacheable(value = "valuteInfoByISOCharCode", key = "#isoCharCode")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "circuitFallbackValuteInfoByISOCharCode")
    public ValuteInfo getValuteInfoByISOCharCode(String isoCharCode) throws JsonProcessingException, CurrencyNotExistException {
        logger.info("Method 'getValuteInfoByISOCharCode': started");

        var response = restClient.get()
                .uri("/scripts/XML_valFull.asp")
                .retrieve()
                .body(String.class);
        logger.debug("Method 'getValuteInfoByISOCharCode': currencies info is received");

        ValuteInfo valuteInfo = xmlMapper.readValue(response, AllValutes.class).getValutes().stream()
                .filter(v -> Objects.equals(v.getIsoCharCode(), isoCharCode))
                .findFirst()
                .orElse(null);
        if (valuteInfo == null) throw new CurrencyNotExistException(isoCharCode);
        logger.debug("Method 'getValuteInfoByISOCharCode': currency is found");

        logger.info("Method 'getValuteInfoByISOCharCode': finished");
        return valuteInfo;
    }

    public double calculateAmountBetweenCurrencies(Valute currencyFrom, double amount, Valute currencyTo) {
        return currencyTo.getValue() / (currencyFrom.getValue() * amount);
    }

    public Valute circuitFallbackCurrencyCursByDate(LocalDate date, String isoCharCode, Throwable throwable) {
        logger.error("Circuit breaker activated for 'getCurrenciesCursesByDate': {}", throwable.getMessage());
        throw new ServiceUnavailableException("Currency service is currently unavailable. Please try again later.");
    }

    public ValuteInfo circuitFallbackValuteInfoByISOCharCode(String isoCharCode, Throwable throwable) {
        logger.error("Circuit breaker activated for 'getValuteInfoByISOCharCode': {}", throwable.getMessage());
        throw new ServiceUnavailableException("Currency service is currently unavailable. Please try again later.");
    }

}
