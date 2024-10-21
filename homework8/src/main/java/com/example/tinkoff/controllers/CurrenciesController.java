package com.example.tinkoff.controllers;

import com.example.tinkoff.dto.ConvertRequest;
import com.example.tinkoff.dto.ConvertResponse;
import com.example.tinkoff.models.Rate;
import com.example.tinkoff.models.Valute;
import com.example.tinkoff.models.ValuteInfo;
import com.example.tinkoff.services.ValuteService;
import com.example.tinkoff.utilities.ControllersAdvice;
import com.example.tinkoff.utilities.ControllersAdvice.ErrorMessage;
import com.example.tinkoff.utilities.CurrencyNotExistException;
import com.example.tinkoff.utilities.CurrencyNotFoundException;
import com.example.tinkoff.utilities.ServiceUnavailableException;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Validated
@RestController
@RequestMapping("/currencies")
public class CurrenciesController {

    @Autowired
    private ValuteService valuteService;

    @Operation(summary = "Get currency rate by date", description = "Provide a date and ISO char code to look up a specific currency rate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved currency rate",
                    content = @Content(schema = @Schema(implementation = Valute.class))),
            @ApiResponse(responseCode = "400", description = "Invalid date or ISO char code supplied"),
            @ApiResponse(responseCode = "404", description = "Currency not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/rates/{code}")
    public ResponseEntity<Rate> getCurrenciesRate(@NotBlank @PathVariable("code") String isoCharCode) throws JsonProcessingException, CurrencyNotExistException, CurrencyNotFoundException {
        var date = LocalDate.now();
        valuteService.getValuteInfoByISOCharCode(isoCharCode);
        var currency = valuteService.getCurrencyCursByDate(date, isoCharCode);
        var rate = new Rate(currency.getCharCode(), currency.getVunitRate());
        return ResponseEntity.ok(rate);
    }

    @Operation(summary = "Get valute info by ISO char code", description = "Provide an ISO char code to look up a specific valute info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved valute info",
                    content = @Content(schema = @Schema(implementation = ValuteInfo.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ISO char code supplied"),
            @ApiResponse(responseCode = "404", description = "Valute not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/convert")
    public ResponseEntity<ConvertResponse> getCurrencyConvert(@RequestBody ConvertRequest convertRequest) throws JsonProcessingException, CurrencyNotExistException, CurrencyNotFoundException {
        var fromCurrencyCode = convertRequest.getFromCurrency();
        var toCurrencyCode = convertRequest.getToCurrency();

        valuteService.getValuteInfoByISOCharCode(fromCurrencyCode);
        valuteService.getValuteInfoByISOCharCode(toCurrencyCode);

        var date = LocalDate.now();

        var fromCurrency = valuteService.getCurrencyCursByDate(date, fromCurrencyCode);
        var toCurrency = valuteService.getCurrencyCursByDate(date, toCurrencyCode);

        var amount = valuteService.calculateAmountBetweenCurrencies(fromCurrency, convertRequest.getAmount(), toCurrency);
        return ResponseEntity.ok(new ConvertResponse(convertRequest.getFromCurrency(), convertRequest.getToCurrency(), amount));
    }
}
