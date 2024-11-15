package com.example.tinkoff.controllers;

import com.example.tinkoff.configurations.XmlMapperConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.example.tinkoff.dto.ConvertRequest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the {@link CurrenciesController}
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.yml")
public class CurrenciesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final XmlMapper utilXmlMapper = XmlMapperConfiguration.xmlMapper();

    // К моменту написания комментария CircuitBreaker в CurrencyService перехватывает исключения CurrencyNotExistException
    // и CurrencyNotFoundException, хотя в конфиге в application.yml они вроде как исключены (поле ignoreExceptions).
    // Из-за этого в контроллере срабатывает неверный ExceptionHandler и поэтому только первый тест с корректными
    // данными проходит. У меня только 2 мысли на данный момент:
    //  - либо по какой-то причине не применился конфиг
    //  - либо сам конфиг написан неверно

    private static Stream<Arguments> ratesParameters_allSituations() {
        return Stream.of(
                Arguments.of("AUD", status().isOk()),
                Arguments.of("A", status().isBadRequest()),
                Arguments.of("", status().isBadRequest()),
                Arguments.of(null, status().isBadRequest())
        );
    }

    @ParameterizedTest
    @MethodSource("ratesParameters_allSituations")
    public void getCurrenciesRate(String code, ResultMatcher expectedResult) throws Exception {
        mockMvc.perform(get("/currencies/rates/{0}", code))
                .andExpect(expectedResult);
    }


    private static Stream<Arguments> convertParameters_allSituations() throws JsonProcessingException {
        return Stream.of(
            Arguments.of(utilXmlMapper.writeValueAsString(new ConvertRequest("AUD", "GBP", 1)), status().isOk()),
            Arguments.of(utilXmlMapper.writeValueAsString(new ConvertRequest("AUD", "GBP", 0)), status().isBadRequest()),
            Arguments.of(utilXmlMapper.writeValueAsString(new ConvertRequest("AUD", "", 1)), status().isBadRequest()),
            Arguments.of(utilXmlMapper.writeValueAsString(new ConvertRequest("AUD", "", 0)), status().isBadRequest()),
            Arguments.of(utilXmlMapper.writeValueAsString(new ConvertRequest("", "GBP", 1)), status().isBadRequest()),
            Arguments.of(utilXmlMapper.writeValueAsString(new ConvertRequest("", "GBP", 0)), status().isBadRequest()),
            Arguments.of(utilXmlMapper.writeValueAsString(new ConvertRequest("", "", 1)), status().isBadRequest()),
            Arguments.of(utilXmlMapper.writeValueAsString(new ConvertRequest("", "", 0)), status().isBadRequest())
        );
    }

    @ParameterizedTest
    @MethodSource("convertParameters_allSituations")
    public void getCurrencyConvert(String content, ResultMatcher expectedResult) throws Exception {
        mockMvc.perform(post("/currencies/convert")
                .content(content)
                .contentType(MediaType.APPLICATION_XML))
            .andExpect(expectedResult);
    }
}
