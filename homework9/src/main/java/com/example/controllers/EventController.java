package com.example.controllers;

import com.example.models.Event;
import com.example.services.KudaGOService;
import com.example.tinkoff.dto.ConvertRequest;
import com.example.tinkoff.dto.ConvertResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.annotations.LogExecutionTime;
import com.example.dto.PossibleEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class EventController {

    @Autowired
    @Qualifier("currency-service")
    private RestClient restClient;

    @Autowired
    private KudaGOService kudaGOService;

    @GetMapping("/v1.1/events")
    public Mono<ResponseEntity<List<PossibleEvent>>> getPossibleEventsMono(
            @RequestParam @NotNull double budget,
            @RequestParam @NotBlank String currency,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateTo
    ) {
        Flux<Event> eventsFlux = kudaGOService.getPossibleEventsFlux(dateFrom, dateTo);

        Mono<ConvertResponse> convertMono = Mono.just(Objects.requireNonNull(restClient.post()
                .uri("/convert")
                .body(new ConvertRequest(currency, "RUB", budget))
                .retrieve()
                .body(ConvertResponse.class)));

        return Mono.zip(eventsFlux.collectList(), convertMono)
                .flatMap(tuple -> {
                    List<Event> events = tuple.getT1();
                    ConvertResponse convert = tuple.getT2();
                    double convertedAmount = convert.getConvertedAmount();
                    return kudaGOService.filterEventsByBudgetFlux(Flux.fromIterable(events), convertedAmount).collectList();
                })
                .map(filteredEvents -> filteredEvents.stream()
                        .map(event -> new PossibleEvent(event.getName(), event.getDates(), event.getMinCost(), event.getMaxCost()))
                        .collect(Collectors.toList()))
                .map(ResponseEntity::ok);
    }

    @LogExecutionTime
    @GetMapping("/v1.0/events")
    public ResponseEntity<List<PossibleEvent>> getPossibleEvents(
            @RequestParam @NotNull double budget,
            @RequestParam @NotBlank String currency,
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
