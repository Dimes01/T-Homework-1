package com.example.tinkoff.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Valute {
    @JacksonXmlProperty(isAttribute = true, localName = "ID")
    @NotNull
    private String id;

    @JacksonXmlProperty(localName = "NumCode")
    @Positive
    private String numCode;

    @JacksonXmlProperty(localName = "CharCode")
    @NotBlank
    private String charCode;

    @JacksonXmlProperty(localName = "Nominal")
    @Positive
    private int nominal;

    @JacksonXmlProperty(localName = "Name")
    @NotBlank
    private String name;

    @JacksonXmlProperty(localName = "Value")
    @Positive
    private double value;

    @JacksonXmlProperty(localName = "VunitRate")
    @Positive
    private double vunitRate;
}
