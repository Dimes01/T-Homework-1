package com.example.tinkoff.controllers;

import com.example.tinkoff.dto.ConvertRequest;
import com.example.tinkoff.dto.ConvertResponse;
import com.example.tinkoff.models.Rate;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController("/currencies")
public class CurrenciesController {
    
//    @GetMapping("/rates/{code}")
//    public ResponseEntity<Rate> getCurrenciesRate(@NotBlank @PathVariable("code") String isoCharCode) {
//
//    }
//
//
//    @PostMapping("/convert")
//    public ResponseEntity<ConvertResponse> getCurrencyConvert(@RequestBody ConvertRequest convertRequest) {
//
//    }
}
