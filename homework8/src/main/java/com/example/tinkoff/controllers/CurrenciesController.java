package com.example.tinkoff.controllers;

import com.example.tinkoff.dto.ConvertRequest;
import com.example.tinkoff.dto.ConvertResponse;
import com.example.tinkoff.models.Rate;
import com.example.tinkoff.models.Valute;
import com.example.tinkoff.models.ValuteCurs;
import com.example.tinkoff.models.ValuteInfo;
import com.example.tinkoff.services.ValuteService;
import com.example.tinkoff.utilities.EntityNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Objects;

@Validated
@RestController("/currencies")
public class CurrenciesController {
    @Autowired
    private ValuteService valuteService;
    
    @GetMapping("/rates/{code}")
    public ResponseEntity<Rate> getCurrenciesRate(@NotBlank @PathVariable("code") String isoCharCode) throws JsonProcessingException, EntityNotFoundException {
        var date = LocalDate.now();
        var currencies = valuteService.getCurrenciesCursesByDate(date);
        if (currencies == null) throw new EntityNotFoundException(ValuteCurs.class);
        Valute currency = currencies
            .getValutes().stream()
            .filter(c -> Objects.equals(c.getCharCode(), isoCharCode))
            .findFirst().get();
        var rate = new Rate(currency.getCharCode(), Double.parseDouble(currency.getVunitRate()));
        return ResponseEntity.ok(rate);
    }


    @PostMapping("/convert")
    public ResponseEntity<ConvertResponse> getCurrencyConvert(@RequestBody ConvertRequest convertRequest) throws JsonProcessingException, EntityNotFoundException {
        var date = LocalDate.now();
        var currencies = valuteService.getCurrenciesCursesByDate(date);
        if (currencies == null) throw new EntityNotFoundException(ValuteCurs.class);
        Valute fromValute = currencies
            .getValutes().stream()
            .filter(c -> Objects.equals(c.getCharCode(), convertRequest.getFromCurrency()))
            .findFirst().get();
        Valute toValute = currencies
            .getValutes().stream()
            .filter(c -> Objects.equals(c.getCharCode(), convertRequest.getToCurrency()))
            .findFirst().get();
        var amount = valuteService.calculateAmountBetweenCurrencies(fromValute, convertRequest.getAmount(), toValute);
        return ResponseEntity.ok(new ConvertResponse(convertRequest.getFromCurrency(), convertRequest.getToCurrency(), amount));
    }
}
