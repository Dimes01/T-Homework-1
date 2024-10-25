package com.example.services;

import com.example.models.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

@Service
public class KudaGOService {

    private final RestClient restClient;
    private final Semaphore semaphore;

    private final Logger logger = LoggerFactory.getLogger(KudaGOService.class);

    @Autowired
    public KudaGOService(
            @Qualifier("kudago-service") RestClient restClient,
            @Qualifier("semaphore") Semaphore semaphore) {
        this.restClient = restClient;
        this.semaphore = semaphore;
    }

    public List<Event> getPossibleEvents(Date from, Date to) {
        logger.info("Method 'getEvents' started");
        int page = 1, page_size = 100;
        boolean has_next = true;
        List<Event> result = new ArrayList<>();
        while (has_next) {
            int pageForRequest = page;
            Event[] events = getEventsPage(pageForRequest, page_size, from, to).body(Event[].class);
            result.addAll(events != null ? List.of(events) : List.of());
            has_next = events != null && events.length == page_size;
            if (!has_next)
                logger.debug("Method 'getEvents': response for events on page {} is null", page);
            ++page;
        }

        logger.info("Method 'getEvents' finished");
        return result;
    }

    public Flux<Event> getPossibleEventsFlux(Date from, Date to) {
        logger.info("Method 'getPossibleEventsFlux' started");
        var result = Flux.defer(() -> Flux.range(1, Integer.MAX_VALUE)
                .concatMap(page -> getEventsPageMono(getEventsPage(page, 100, from, to)))
                .takeUntil(List::isEmpty)
                .flatMap(Flux::fromIterable));
        logger.info("Method 'getPossibleEventsFlux' finished");
        return result;

    }

    public List<Event> filterEventsByBudget(List<Event> events, double budget) {
        logger.info("Method 'filterEventsByBudget' started");
        var result = events.stream()
                .filter(event -> event.getMinCost() <= budget)
                .collect(Collectors.toList());
        logger.info("Method 'filterEventsByBudget' started");
        return result;
    }

    public Flux<Event> filterEventsByBudgetFlux(Flux<Event> events, double budget) {
        logger.info("Method 'filterEventsByBudgetFlux' started");
        var result = events.filter(event -> event.getMinCost() <= budget);
        logger.info("Method 'filterEventsByBudgetFlux' finished");
        return result;
    }


    private Mono<List<Event>> getEventsPageMono(RestClient.ResponseSpec responseSpec) {
        logger.info("Method 'getEventsPageMono' started");
        var result = Mono.just(List.of(Objects.requireNonNull(responseSpec.body(Event[].class))));
        logger.info("Method 'getEventsPageMono' finished");
        return result;
    }

    private RestClient.ResponseSpec getEventsPage(int page, int pageSize, Date from, Date to) {
        logger.info("Method 'getEventsPage' started");
        RestClient.ResponseSpec responseSpec;
        try {
            semaphore.acquire();
            URI uri = UriComponentsBuilder.fromPath("/public-api/v1.4/events/")
                    .queryParam("page", page)
                    .queryParam("page_size", pageSize)
                    .queryParam("fields", "id,price,dates")
                    .queryParam("actual_since", from.getTime())
                    .queryParam("actual_until", to.getTime())
                    .buildAndExpand().toUri();
            responseSpec = restClient.get().uri(uri).retrieve();
            semaphore.release();
            logger.debug("Method 'getEventsPage': events is receive");
        } catch (InterruptedException e) {
            logger.error("Method 'getEventsPage' interrupted by semaphore");
            responseSpec = null;
        }
        logger.info("Method 'getEventsPage' finished");
        return responseSpec;
    }
}
