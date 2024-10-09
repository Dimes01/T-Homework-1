package com.example.tinkoff.services;

import com.example.tinkoff.Homework8Application;
import com.example.tinkoff.models.AllValutes;
import com.example.tinkoff.models.Valute;
import com.example.tinkoff.models.ValuteCurs;
import com.example.tinkoff.models.ValuteInfo;
import com.example.tinkoff.configurations.XmlMapperConfiguration;
import com.example.tinkoff.utilities.CurrencyNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.web.client.RestClient.*;

@SpringBootTest(classes = Homework8Application.class)
class ValuteServiceTest {
    @Mock
    private RestClient restClient;
    @Mock
    private XmlMapper xmlMapper;
    @Mock
    private RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private RequestHeadersSpec requestHeadersSpec;
    @Mock
    private ResponseSpec responseSpec;

    @InjectMocks
    private ValuteService valuteService;

    private final XmlMapper utilXmlMapper = XmlMapperConfiguration.xmlMapper();

    private static final ValuteCurs valuteCursWithValutes = new ValuteCurs("2024-10-04", "Foreign Currency Market", Arrays.asList(
            new Valute("R01010", "036", "AUD", 1, "Австралийский доллар", 16.0102, 16.0102),
            new Valute("R01035", "826", "GBP", 1, "Фунт стерлингов Соединенного королевства", 43.8254, 43.8254)
    ));
    private static final AllValutes allValutes = new AllValutes("Foreign Currency Market Lib", Arrays.asList(
            new ValuteInfo("R01010", "Австралийский доллар", "Australian Dollar", 1, "R01010", 36, "AUD"),
            new ValuteInfo("R01015", "Австрийский шиллинг", "Austrian Shilling", 1000, "R01015", 40, "ATS")
    ));

    private String simpleResponse;

    public ValuteServiceTest() throws JsonProcessingException {
    }

    @BeforeEach
    public void set_up() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(Mockito.anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    public void getCurrencyCursByDate_notNullInformation_notNullObject() throws JsonProcessingException, CurrencyNotFoundException {
        // Arrange
        var expectedObject = valuteCursWithValutes.getValutes().getFirst();
        simpleResponse = utilXmlMapper.writeValueAsString(expectedObject);
        when(responseSpec.body(eq(String.class))).thenReturn(simpleResponse);
        when(xmlMapper.readValue(Mockito.anyString(), eq(ValuteCurs.class))).thenReturn(valuteCursWithValutes);
        var date = LocalDate.parse(valuteCursWithValutes.getDate());

        // Act
        var response = valuteService.getCurrencyCursByDate(date, "AUD");

        // Assert
        assertEquals(expectedObject, response);
    }


    private static Stream<Class<? extends Exception>> exceptionProvider() {
        return Stream.of(
                HttpClientErrorException.class,
                RestClientResponseException.class,
                JsonProcessingException.class,
                CurrencyNotFoundException.class
        );
    }

    @ParameterizedTest
    @MethodSource("exceptionProvider")
    public void getCurrencyCursByDate_notNullInformation_throwException(Class<? extends Exception> exceptionClass) throws JsonProcessingException {
        // Arrange
        var expectedObject = valuteCursWithValutes.getValutes().getFirst();
        simpleResponse = utilXmlMapper.writeValueAsString(expectedObject);
        var date = LocalDate.parse(valuteCursWithValutes.getDate());

        if (exceptionClass == HttpClientErrorException.class) {
            when(requestHeadersSpec.retrieve()).thenThrow(exceptionClass);
        } else if (exceptionClass == RestClientResponseException.class) {
            when(responseSpec.body(eq(String.class))).thenThrow(exceptionClass);
        } else {
            when(responseSpec.body(eq(String.class))).thenReturn(simpleResponse);
            var whenReadValue = when(xmlMapper.readValue(Mockito.anyString(), eq(ValuteCurs.class)));
            if (exceptionClass == JsonProcessingException.class) {
                whenReadValue.thenThrow(exceptionClass);
            } else if (exceptionClass == CurrencyNotFoundException.class) {
                whenReadValue.thenReturn(new ValuteCurs("2024-10-04", "Foreign Currency Market", Collections.emptyList()));
            }
        }

        // Assert & Act
        assertThrows(exceptionClass, () -> valuteService.getCurrencyCursByDate(date, "AUD"));
    }

    private static Stream<Arguments> calculateAmountBetweenCurrencies_allSituations() {
        return Stream.of(
                Arguments.of(valuteCursWithValutes.getValutes().get(0), valuteCursWithValutes.getValutes().get(1), 10)
        );
    }

    @ParameterizedTest
    @MethodSource("calculateAmountBetweenCurrencies_allSituations")
    public void test_calculateAmountBetweenCurrencies(Valute currencyFrom, Valute currencyTo, double amount) {
        var currencyService = new ValuteService();
        double expectedAmount = currencyTo.getValue() / (currencyFrom.getValue() * amount);
        assertEquals(expectedAmount, currencyService.calculateAmountBetweenCurrencies(currencyFrom, amount, currencyTo));
    }
}