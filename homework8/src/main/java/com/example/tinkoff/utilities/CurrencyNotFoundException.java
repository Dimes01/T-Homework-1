package com.example.tinkoff.utilities;

public class CurrencyNotFoundException extends CurrencyException {
    public CurrencyNotFoundException(String isoCharCode) {
        this.isoCharCode = isoCharCode;
    }
}
