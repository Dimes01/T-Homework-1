package org.example.homework8.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ConvertRequest {
    @NotBlank(message = "'From currency' must be specified")
    private final String fromCurrency;

    @NotBlank(message = "'To currency' must be specified")
    private final String toCurrency;

    @Positive(message = "'Amount must' be positive")
    private final double amount;
}
