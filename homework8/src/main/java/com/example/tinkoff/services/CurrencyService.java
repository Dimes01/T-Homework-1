package com.example.tinkoff.services;

import com.example.tinkoff.models.AllCurrencies;
import com.example.tinkoff.models.Currency;
import com.example.tinkoff.models.CurrencyCurs;
import com.example.tinkoff.models.CurrencyInfo;
import com.example.tinkoff.utilities.CurrencyNotExistException;
import com.example.tinkoff.utilities.CurrencyNotFoundException;
import com.example.tinkoff.utilities.ServiceUnavailableException;
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
public class CurrencyService {

    @Autowired
    private RestClient restClient;

    @Autowired
    private XmlMapper xmlMapper;

    private final String dateFormat = "dd/MM/yyyy";

    private final Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    @Cacheable(value = "currenciesCursesByDate")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "circuitFallbackCurrencyCursByDate")
    public Currency getCurrencyCursByDate(LocalDate date, String isoCharCode) throws JsonProcessingException, CurrencyNotFoundException {
        logger.info("Method 'getCurrencyCursByDate': started");

        var dateFormatted = date.format(DateTimeFormatter.ofPattern(dateFormat));
        var response = restClient.get()
                .uri("/scripts/XML_daily.asp?date_req=" + dateFormatted)
                .retrieve()
                .body(String.class);
        logger.debug("Method 'getCurrencyCursByDate': currencies curses is received");

        Currency currency = xmlMapper.readValue(response, CurrencyCurs.class)
                .getCurrencies().stream()
                .filter(c -> Objects.equals(c.getCharCode(), isoCharCode))
                .findFirst().orElse(null);
        if (currency == null) throw new CurrencyNotFoundException(isoCharCode);
        logger.debug("Method 'getCurrencyCursByDate': currencies curses is converted");

        logger.info("Method 'getCurrencyCursByDate': finished");
        return currency;
    }

    @Cacheable(value = "valuteInfoByISOCharCode", key = "#isoCharCode")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "circuitFallbackCurrencyInfoByISOCharCode")
    public CurrencyInfo getCurrencyInfoByISOCharCode(String isoCharCode) throws JsonProcessingException, CurrencyNotExistException {
        logger.info("Method 'getCurrencyInfoByISOCharCode': started");

        var response = restClient.get()
                .uri("/scripts/XML_valFull.asp")
                .retrieve()
                .body(String.class);
        logger.debug("Method 'getCurrencyInfoByISOCharCode': currencies info is received");

        CurrencyInfo currencyInfo = xmlMapper.readValue(response, AllCurrencies.class).getValutes().stream()
                .filter(v -> Objects.equals(v.getIsoCharCode(), isoCharCode))
                .findFirst()
                .orElse(null);
        if (currencyInfo == null) throw new CurrencyNotExistException(isoCharCode);
        logger.debug("Method 'getCurrencyInfoByISOCharCode': currency is found");

        logger.info("Method 'getCurrencyInfoByISOCharCode': finished");
        return currencyInfo;
    }

    public double calculateAmountBetweenCurrencies(Currency currencyFrom, double amount, Currency currencyTo) {
        return currencyTo.getValue() / (currencyFrom.getValue() * amount);
    }

    public Currency circuitFallbackCurrencyCursByDate(LocalDate date, String isoCharCode, Throwable throwable) {
        logger.error("Circuit breaker activated for 'getCurrencyCursByDate': {}", throwable.getMessage());
        throw new ServiceUnavailableException("Currency service is currently unavailable. Please try again later.");
    }

    public CurrencyInfo circuitFallbackCurrencyInfoByISOCharCode(String isoCharCode, Throwable throwable) {
        logger.error("Circuit breaker activated for 'getCurrencyInfoByISOCharCode': {}", throwable.getMessage());
        throw new ServiceUnavailableException("Currency service is currently unavailable. Please try again later.");
    }

}
