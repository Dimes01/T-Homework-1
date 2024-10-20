package com.example.tinkoff.utilities;

import lombok.Getter;

@Getter
public abstract class CurrencyException extends RuntimeException {
    protected String isoCharCode;
}

