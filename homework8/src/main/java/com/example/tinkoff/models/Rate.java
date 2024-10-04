package com.example.tinkoff.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class Rate {
    @NotBlank
    private String currency;

    @Positive
    private double rate;
}
