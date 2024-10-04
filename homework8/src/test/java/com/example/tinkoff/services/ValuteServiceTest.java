package com.example.tinkoff.services;

import com.example.tinkoff.Homework8Application;
import com.example.tinkoff.models.Valute;
import com.example.tinkoff.models.ValuteCurs;
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

    private XmlMapper localXmlMapper = XmlMapperConfiguration.xmlMapper();

    private static final ValuteCurs valuteCursWithValutes = new ValuteCurs(LocalDate.parse("2024-10-04"), "Foreign Currency Market", Arrays.asList(
            new Valute("R01010", "036", "AUD", 1, "Австралийский доллар", 16.0102, 16.0102),
            new Valute("R01035", "826", "GBP", 1, "Фунт стерлингов Соединенного королевства", 43.8254, 43.8254)
    ));
    private static final ValuteCurs valuteCursWithoutValutes = new ValuteCurs(LocalDate.parse("2024-10-05"), "Foreign Currency Market", new LinkedList<>());
    private String simpleResponse = localXmlMapper.writeValueAsString(valuteCursWithValutes);

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

    @Test
    void getCurrenciesCursesByDate_notNullInformation_throwHttpClientErrorException() throws JsonProcessingException {
        // Arrange
        simpleResponse = localXmlMapper.writeValueAsString(valuteCursWithValutes);
        when(requestHeadersSpec.retrieve()).thenThrow(HttpClientErrorException.class);

        // Assert & Act
        assertThrows(HttpClientErrorException.class, () -> valuteService.getCurrenciesCursesByDate(valuteCursWithValutes.getDate()));
    }

    @Test
    void getCurrenciesCursesByDate_notNullInformation_throwRestClientResponseException() throws JsonProcessingException {
        // Arrange
        simpleResponse = localXmlMapper.writeValueAsString(valuteCursWithValutes);
        when(responseSpec.body(eq(String.class))).thenThrow(RestClientResponseException.class);

        // Assert & Act
        assertThrows(RestClientResponseException.class, () -> valuteService.getCurrenciesCursesByDate(valuteCursWithValutes.getDate()));
    }

    @Test
    void getCurrenciesCursesByDate_notNullInformation_throwJsonProcessingException() throws JsonProcessingException {
        // Arrange
        simpleResponse = localXmlMapper.writeValueAsString(valuteCursWithValutes);
        when(responseSpec.body(eq(String.class))).thenReturn(simpleResponse);
        when(xmlMapper.readValue(Mockito.anyString(), eq(ValuteCurs.class))).thenThrow(JsonProcessingException.class);

        // Assert & Act
        assertThrows(JsonProcessingException.class, () -> valuteService.getCurrenciesCursesByDate(valuteCursWithValutes.getDate()));
    }
}