package com.example.services;

import com.example.models.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KudaGOService {
    @Autowired
    @Qualifier("kudago-service")
    private RestClient restClient;

    private final Logger logger = LoggerFactory.getLogger(KudaGOService.class);


    public List<Event> getPossibleEvents(Date from, Date to) {
        logger.info("Method 'getEvents' started");
        int page = 1, page_size = 100;
        boolean has_next = true;
        List<Event> result = new ArrayList<>();
        while (has_next) {
            int pageForRequest = page;
            Event[] events = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/public-api/v1.4/events/")
                            .queryParam("page", pageForRequest)
                            .queryParam("page_size", page_size)
                            .queryParam("fields", "id,price,dates")
                            .queryParam("actual_since", from.getTime())
                            .queryParam("actual_until", to.getTime())
                            .build())
                    .retrieve()
                    .body(Event[].class);
            result.addAll(events != null ? List.of(events) : List.of());
            has_next = !result.isEmpty();
            if (!has_next)
                logger.debug("Method 'getEvents': response for events on page {} is null", page);
            ++page;
        }

        logger.info("Method 'getEvents' finished");
        return result;
    }

    public List<Event> filterEventsByBudget(List<Event> events, double budget) {
        return events.stream()
                .filter(event -> event.getMinCost() <= budget)
                .collect(Collectors.toList());
    }
}
