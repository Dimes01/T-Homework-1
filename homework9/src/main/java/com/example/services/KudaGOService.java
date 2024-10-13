package com.example.services;

import com.example.models.Category;
import com.example.models.Event;
import com.example.models.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class KudaGOService {
    private final RestClient restClient;
    private final Logger logger = LoggerFactory.getLogger(KudaGOService.class);

    @Autowired
    public KudaGOService(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<Category> getCategories() {
        logger.info("Method 'getCategories' started");
        Category[] categories = restClient.get()
                .uri("/public-api/v1.4/place-categories/")
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
                .uri("/public-api/v1.4/locations/?fields=slug,name,timezone,coords,language,currency")
                .retrieve()
                .body(Location[].class);
        List<Location> result = locations != null ? List.of(locations) : List.of();
        if (result.isEmpty())
            logger.warn("Method 'getLocations': response for locations is null");
        logger.info("Method 'getLocations' finished");
        return result;
    }

    public List<Event> getEvents(Date from, Date to) {
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
}
