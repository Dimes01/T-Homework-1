package com.example.tinkoff.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValuteCurs {

    @JacksonXmlProperty(isAttribute = true, localName = "Date")
    private String date;

    @JacksonXmlProperty(isAttribute = true, localName = "name")
    private String name;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Valute")
    private List<Valute> valutes;
}
