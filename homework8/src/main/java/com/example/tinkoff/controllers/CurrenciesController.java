package com.example.tinkoff.controllers;

import com.example.tinkoff.dto.ConvertRequest;
import com.example.tinkoff.dto.ConvertResponse;
import com.example.tinkoff.models.Rate;
import com.example.tinkoff.services.ValuteService;
import com.example.tinkoff.utilities.CurrencyNotExistException;
import com.example.tinkoff.utilities.CurrencyNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Objects;

@Validated
@RestController("/currencies")
public class CurrenciesController {

    public record ErrorMessage(String message, int code) {}

    @Autowired
    private ValuteService valuteService;
    
    @GetMapping("/rates/{code}")
    public ResponseEntity<Rate> getCurrenciesRate(@NotBlank @PathVariable("code") String isoCharCode) throws JsonProcessingException, CurrencyNotExistException, CurrencyNotFoundException {
        var date = LocalDate.now();
        var currencyInfo = valuteService.getValuteInfoByISOCharCode(isoCharCode);
        var currency = valuteService.getCurrencyCursByDate(date, isoCharCode);
        var rate = new Rate(currency.getCharCode(), currency.getVunitRate());
        return ResponseEntity.ok(rate);
    }

    @PostMapping("/convert")
    public ResponseEntity<ConvertResponse> getCurrencyConvert(@RequestBody ConvertRequest convertRequest) throws JsonProcessingException, CurrencyNotExistException, CurrencyNotFoundException {
        var fromCurrencyCode = convertRequest.getFromCurrency();
        var toCurrencyCode = convertRequest.getToCurrency();
        var currencyFromInfo = valuteService.getValuteInfoByISOCharCode(fromCurrencyCode);
        var currencyToInfo = valuteService.getValuteInfoByISOCharCode(toCurrencyCode);
        var date = LocalDate.now();
        var fromCurrency = valuteService.getCurrencyCursByDate(date, fromCurrencyCode);
        var toCurrency = valuteService.getCurrencyCursByDate(date, toCurrencyCode);
        var amount = valuteService.calculateAmountBetweenCurrencies(fromCurrency, convertRequest.getAmount(), toCurrency);
        return ResponseEntity.ok(new ConvertResponse(convertRequest.getFromCurrency(), convertRequest.getToCurrency(), amount));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message;
        if (Objects.equals(e.getParameter().getParameterName(), "amount"))
            message = "Amount must be positive integer";
        else
            message = e.getParameter().getParameterName() + "must not be null or empty";
        return new ResponseEntity<>(
                new ErrorMessage(message, HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({JsonProcessingException.class})
    public ResponseEntity<ErrorMessage> handleJsonProcessingException(JsonProcessingException e) {
        return new ResponseEntity<>(
            new ErrorMessage(e.getMessage(), HttpStatus.BAD_REQUEST.value()),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({CurrencyNotExistException.class})
    public ResponseEntity<ErrorMessage> handleCurrencyNotExistException(CurrencyNotExistException e) {
        return new ResponseEntity<>(
            new ErrorMessage(String.format("Currency with code '{0}' is not existed", e.getIsoCharCode()), HttpStatus.BAD_REQUEST.value()),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({CurrencyNotFoundException.class})
    public ResponseEntity<ErrorMessage> CurrencyNotFoundException(CurrencyNotExistException e) {
        return new ResponseEntity<>(
            new ErrorMessage(String.format("Currency with code '{0}' is not found", e.getIsoCharCode()), HttpStatus.NOT_FOUND.value()),
            HttpStatus.NOT_FOUND
        );
    }
}
