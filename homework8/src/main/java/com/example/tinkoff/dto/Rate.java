package com.example.tinkoff.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Rate {
    @NotBlank
    private String currency;

    @Positive
    private double rate;
}
