package com.example.tinkoff.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

    @BeforeEach
    public void setup() {

    }

    @Test
    public void getCurrenciesRate() throws Exception {
        mockMvc.perform(get("/rates/{0}", "AUD"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void getCurrencyConvert() throws Exception {
        String convertRequest = """
                {
                    "fromCurrency": "AUD",
                    "toCurrency": "GBP",
                    "amount": 1
                }""";

        mockMvc.perform(post("/convert")
                        .content(convertRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
