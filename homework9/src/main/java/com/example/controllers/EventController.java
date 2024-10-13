package com.example.controllers;

import com.example.models.Event;
import com.example.services.KudaGOService;
import org.example.homework8.dto.ConvertRequest;
import org.example.homework8.dto.ConvertResponse;
import org.example.homework9.dto.PossibleEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class EventController {

    @Autowired
    @Qualifier("currency-service")
    private RestClient restClient;

    @Autowired
    private KudaGOService kudaGOService;

    @GetMapping("/events")
    public ResponseEntity<List<PossibleEvent>> getPossibleEvents(
            @RequestParam double budget,
            @RequestParam String currency,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateTo
    ) {
        CompletableFuture<List<Event>> eventsFuture = CompletableFuture.supplyAsync(() ->
            kudaGOService.getPossibleEvents(dateFrom, dateTo)
        );

        CompletableFuture<ConvertResponse> convertFuture = CompletableFuture.supplyAsync(() -> restClient.post()
                .uri("/convert")
                .body(new ConvertRequest(currency, "RUB", budget))
                .retrieve()
                .body(ConvertResponse.class)
        );

        List<PossibleEvent> result = eventsFuture.thenCombine(convertFuture, (events, convert) ->
                kudaGOService.filterEventsByBudget(events, convert.getConvertedAmount()).stream()
                        .map((event) -> new PossibleEvent(event.getName(), event.getDates(), event.getMinCost(), event.getMaxCost()))
                        .toList()).resultNow();

        return ResponseEntity.ok(result);
    }
}
