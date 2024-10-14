package com.example.services;

import com.example.models.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
            has_next = !result.isEmpty();
            if (!has_next)
                logger.debug("Method 'getEvents': response for events on page {} is null", page);
            ++page;
        }

        logger.info("Method 'getEvents' finished");
        return result;
    }

    public Flux<Event> getPossibleEventsFlux(Date from, Date to) {
        return Flux.defer(() -> Flux.range(1, Integer.MAX_VALUE)
                .concatMap(page -> getEventsPageMono(getEventsPage(page, 100, from, to)))
                .takeUntil(List::isEmpty)
                .flatMap(Flux::fromIterable));
    }

    public List<Event> filterEventsByBudget(List<Event> events, double budget) {
        return events.stream()
                .filter(event -> event.getMinCost() <= budget)
                .collect(Collectors.toList());
    }

    public Flux<Event> filterEventsByBudget(Flux<Event> events, double budget) {
        return events.filter(event -> event.getMinCost() <= budget);
    }


    private Mono<List<Event>> getEventsPageMono(RestClient.ResponseSpec responseSpec) {
        return Mono.just(List.of(Objects.requireNonNull(responseSpec.body(Event[].class))));
    }

    private RestClient.ResponseSpec getEventsPage(int page, int pageSize, Date from, Date to) {
        try {
            semaphore.acquire();
            RestClient.ResponseSpec responseSpec = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/public-api/v1.4/events/")
                            .queryParam("page", page)
                            .queryParam("page_size", pageSize)
                            .queryParam("fields", "id,price,dates")
                            .queryParam("actual_since", from.getTime())
                            .queryParam("actual_until", to.getTime())
                            .build())
                    .retrieve();
            semaphore.release();
            return responseSpec;
        } catch (InterruptedException e) {
            return null;
        }
    }
}
