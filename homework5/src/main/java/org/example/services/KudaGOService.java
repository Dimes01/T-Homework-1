package org.example.services;
import org.example.models.Category;
import org.example.models.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
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
                .uri("/public-api/v1.4/place-categories?lang=ru")
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
                .uri("/public-api/v1.4/locations?lang=ru&fields=slug,name,timezone,coords,language,currency")
                .retrieve()
                .body(Location[].class);
        List<Location> result = locations != null ? List.of(locations) : List.of();
        if (result.isEmpty())
            logger.warn("Method 'getLocations': response for locations is null");
        logger.info("Method 'getLocations' finished");
        return result;
    }
}
