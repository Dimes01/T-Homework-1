package com.example.tinkoff.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Valute {

    @JacksonXmlProperty(isAttribute = true, localName = "ID")
    private String id;

    @JacksonXmlProperty(localName = "NumCode")
    private String numCode;

    @JacksonXmlProperty(localName = "CharCode")
    private String charCode;

    @JacksonXmlProperty(localName = "Nominal")
    private int nominal;

    @JacksonXmlProperty(localName = "Name")
    private String name;

    @JacksonXmlProperty(localName = "Value")
    private String value;

    @JacksonXmlProperty(localName = "VunitRate")
    private String vunitRate;
}
