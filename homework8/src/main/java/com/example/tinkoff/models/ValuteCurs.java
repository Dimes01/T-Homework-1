package com.example.tinkoff.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValuteCurs {
    @JacksonXmlProperty(isAttribute = true, localName = "Date")
    @NotNull
    private LocalDate date;

    @JacksonXmlProperty(isAttribute = true)
    @NotBlank
    private String name;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Valute")
    @NotNull
    private List<Valute> valutes;
}
