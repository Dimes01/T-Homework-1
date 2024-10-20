package com.example.tinkoff.utilities;

import lombok.Getter;

@Getter
public abstract class CurrencyException extends Exception {
    protected String isoCharCode;
}

