package com.example.tinkoff.controllers;

import com.example.tinkoff.configurations.XmlMapperConfiguration;
import com.example.tinkoff.dto.ConvertRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the {@link CurrenciesController}
 */
@SpringBootTest
@AutoConfigureMockMvc
public class CurrenciesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final XmlMapper xmlMapper = XmlMapperConfiguration.xmlMapper();
    private final ConvertRequest convertRequest = new ConvertRequest("AUD", "GBP", 1);
    private final String convertRequestString = xmlMapper.writeValueAsString(convertRequest);

    public CurrenciesControllerTest() throws JsonProcessingException {
    }


    @Test
    public void getCurrenciesRate_correctParameter_successful() throws Exception {
        mockMvc.perform(get("/rates/{0}", "AUD"))
                .andExpect(status().isOk());
    }


    private static Stream<Arguments> ratesParameters_incorrect() {
        return Stream.of(
                Arguments.of("", status().is4xxClientError()),
                Arguments.of(null, status().is4xxClientError())
        );
    }

    @ParameterizedTest
    @MethodSource("ratesParameters_incorrect")
    public void getCurrenciesRate_incorrectParameter_throwExceptions(String code, ResultMatcher expectedResult) throws Exception {
        mockMvc.perform(get("/rates/{0}", code))
                .andExpect(expectedResult);
    }

    @Test
    public void getCurrencyConvert() throws Exception {
        mockMvc.perform(post("/convert")
                .content(convertRequestString)
                .contentType(MediaType.APPLICATION_XML))
            .andExpect(status().isOk())
            .andDo(print());
    }
}
