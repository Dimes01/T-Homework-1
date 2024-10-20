package org.example.homework8.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ConvertResponse {
    @NotBlank
    private final String fromCurrency;

    @NotBlank
    private final String toCurrency;

    @Positive
    private final double convertedAmount;
}
