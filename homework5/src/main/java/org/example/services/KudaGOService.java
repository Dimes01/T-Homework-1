package org.example.services;

import org.example.homework5.models.Category;
import org.example.homework5.models.Location;
import org.example.models.homework10.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

@Service
public class KudaGOService {
    private final RestClient restClient;
    private final Semaphore semaphore;
    private final Logger logger = LoggerFactory.getLogger(KudaGOService.class);

    @Autowired
    public KudaGOService(RestClient restClient, Semaphore semaphore) {
        this.restClient = restClient;
        this.semaphore = semaphore;
    }

    public List<Category> getCategories() {
        logger.info("Method 'getCategories' started");
        Category[] categories = restClient.get()
                .uri("/public-api/v1.4/place-categories/?lang=ru")
                .retrieve()
                .body(Category[].class);
        List<Category> result = categories != null ? List.of(categories) : List.of();
        if (result.isEmpty())
            logger.warn("Method 'getCategories': response for place categories is null");
        logger.info("Method 'getCategories' finished");
        return result;
    }

    public List<Location> getLocations() {
        logger.info("Method 'getLocations' started");
        Location[] locations = restClient.get()
                .uri("/public-api/v1.4/locations/?lang=ru&fields=slug,name,timezone,coords,language,currency")
                .retrieve()
                .body(Location[].class);
        List<Location> result = locations != null ? List.of(locations) : List.of();
        if (result.isEmpty())
            logger.warn("Method 'getLocations': response for locations is null");
        logger.info("Method 'getLocations' finished");
        return result;
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




