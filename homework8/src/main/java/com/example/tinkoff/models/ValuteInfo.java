package com.example.tinkoff.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ValuteInfo {
    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String engName;

    @Positive
    private int nominal;

    @NotBlank
    private String parentCode;

    @Positive
    private int isoNumCode;

    @NotBlank
    private String isoCharCode;
}
