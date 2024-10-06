package com.example.tinkoff.services;

import com.example.tinkoff.Homework8Application;
import com.example.tinkoff.models.AllValutes;
import com.example.tinkoff.models.Valute;
import com.example.tinkoff.models.ValuteCurs;
import com.example.tinkoff.models.ValuteInfo;
import com.example.tinkoff.utilities.XmlMapperConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
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

    private final XmlMapper localXmlMapper = XmlMapperConfiguration.xmlMapper();

    private static final ValuteCurs valuteCursWithValutes = new ValuteCurs(LocalDate.parse("2024-10-04"), "Foreign Currency Market", Arrays.asList(
            new Valute("R01010", "036", "AUD", 1, "Австралийский доллар", 16.0102, 16.0102),
            new Valute("R01035", "826", "GBP", 1, "Фунт стерлингов Соединенного королевства", 43.8254, 43.8254)
    ));
    private static final ValuteCurs valuteCursWithoutValutes = new ValuteCurs(LocalDate.parse("2024-10-05"), "Foreign Currency Market", new LinkedList<>());
    private static final AllValutes allValutes = new AllValutes("Foreign Currency Market Lib", Arrays.asList(
            new ValuteInfo("R01010", "Австралийский доллар", "Australian Dollar", 1, "R01010", 36, "AUD"),
            new ValuteInfo("R01015", "Австрийский шиллинг", "Austrian Shilling", 1000, "R01015", 40, "ATS")
    ));

    private String simpleResponse;

    ValuteServiceTest() throws JsonProcessingException {
    }

    @BeforeEach
    void set_up() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(Mockito.anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    private static Stream<Arguments> getCurrenciesCursesByDate_positiveCituations() {
        return Stream.of(
            Arguments.of(valuteCursWithValutes),
            Arguments.of(valuteCursWithoutValutes)
        );
    }

    @ParameterizedTest
    @MethodSource("getCurrenciesCursesByDate_positiveCituations")
    void getCurrenciesCursesByDate_notNullInformation_notNullObject(ValuteCurs expected) throws JsonProcessingException {
        // Arrange
        simpleResponse = localXmlMapper.writeValueAsString(expected);
        when(responseSpec.body(eq(String.class))).thenReturn(simpleResponse);
        when(xmlMapper.readValue(Mockito.anyString(), eq(ValuteCurs.class))).thenReturn(expected);

        // Act
        var response = valuteService.getCurrenciesCursesByDate(expected.getDate());

        // Assert
        assertEquals(expected, response);
    }

    private static Stream<Throwable> exceptionProvider() {
        return Stream.of(
                new HttpClientErrorException(HttpStatus.BAD_REQUEST),
                new RestClientResponseException("Test exception", HttpStatus.BAD_REQUEST.value(), "Bad Request", null, null, null),
                new JsonProcessingException("Test exception") {}
        );
    }

    @ParameterizedTest
    @MethodSource("exceptionProvider")
    void getCurrenciesCursesByDate_notNullInformation_throwException(Throwable exception) throws JsonProcessingException {
        // Arrange
        simpleResponse = localXmlMapper.writeValueAsString(valuteCursWithValutes);

        if (exception instanceof HttpClientErrorException) {
            when(requestHeadersSpec.retrieve()).thenThrow(exception);
        } else if (exception instanceof RestClientResponseException) {
            when(responseSpec.body(eq(String.class))).thenThrow(exception);
        } else if (exception instanceof JsonProcessingException) {
            when(responseSpec.body(eq(String.class))).thenReturn(simpleResponse);
            when(xmlMapper.readValue(Mockito.anyString(), eq(ValuteCurs.class))).thenThrow(exception);
        }

        // Assert & Act
        assertThrows(exception.getClass(), () -> valuteService.getCurrenciesCursesByDate(valuteCursWithValutes.getDate()));
    }

    @Test
    void getValuteInfoByISOCharCode_notNullInformation_notNullObject() throws JsonProcessingException {
        // Arrange
        simpleResponse = localXmlMapper.writeValueAsString(allValutes);
        when(responseSpec.body(eq(String.class))).thenReturn(simpleResponse);
        when(xmlMapper.readValue(Mockito.anyString(), eq(AllValutes.class))).thenReturn(allValutes);

        // Act
        var response = valuteService.getValuteInfoByISOCharCode(allValutes.getValutes().getFirst().getIsoCharCode());

        // Assert
        assertEquals(allValutes.getValutes().getFirst(), response);
    }

    @ParameterizedTest
    @MethodSource("exceptionProvider")
    void getValuteInfoByISOCharCode_notNullInformation_throwException(Throwable exception) throws JsonProcessingException {
        // Arrange
        simpleResponse = localXmlMapper.writeValueAsString(allValutes);

        if (exception instanceof HttpClientErrorException) {
            when(requestHeadersSpec.retrieve()).thenThrow(exception);
        } else if (exception instanceof RestClientResponseException) {
            when(responseSpec.body(eq(String.class))).thenThrow(exception);
        } else if (exception instanceof JsonProcessingException) {
            when(responseSpec.body(eq(String.class))).thenReturn(simpleResponse);
            when(xmlMapper.readValue(Mockito.anyString(), eq(AllValutes.class))).thenThrow(exception);
        }

        // Assert & Act
        assertThrows(exception.getClass(), () -> valuteService.getValuteInfoByISOCharCode(allValutes.getValutes().getFirst().getIsoCharCode()));
    }
}