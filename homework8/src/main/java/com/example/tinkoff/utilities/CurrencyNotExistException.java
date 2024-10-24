package com.example.tinkoff.utilities;

public class CurrencyNotExistException extends CurrencyException {
    public CurrencyNotExistException(String isoCharCode) {
        this.isoCharCode = isoCharCode;
    }
}
