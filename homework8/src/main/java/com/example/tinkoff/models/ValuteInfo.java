package com.example.tinkoff.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ValuteInfo {
    @JacksonXmlProperty(localName = "ID", isAttribute = true)
    @NotBlank
    private String id;

    @JacksonXmlProperty(localName = "Name")
    @NotBlank
    private String name;

    @JacksonXmlProperty(localName = "EngName")
    @NotBlank
    private String engName;

    @JacksonXmlProperty(localName = "Nominal")
    @Positive
    private int nominal;

    @JacksonXmlProperty(localName = "ParentCode")
    @NotBlank
    private String parentCode;

    @JacksonXmlProperty(localName = "ISO_Num_Code")
    @Positive
    private int isoNumCode;

    @JacksonXmlProperty(localName = "ISO_Char_Code")
    @NotBlank
    private String isoCharCode;
}
