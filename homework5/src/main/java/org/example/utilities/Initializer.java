package org.example.utilities;

import org.example.models.Category;
import org.example.models.Location;
import org.example.services.KudaGOService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class Initializer implements ApplicationListener<ContextRefreshedEvent> {
    private final KudaGOService kudaGOService;
    private final Storage<Category> categoryStorage;
    private final Storage<Location> locationStorage;
    private final Logger logger = LoggerFactory.getLogger(Initializer.class);
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Autowired
    public Initializer(KudaGOService kudaGOService, Storage<Category> categoryStorage, Storage<Location> locationStorage) {
        this.kudaGOService = kudaGOService;
        this.categoryStorage = categoryStorage;
        this.locationStorage = locationStorage;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info("Initializer and method 'run' are started");

        List<Category> categories = kudaGOService.getCategories();
        categories.forEach(category -> categoryStorage.save(category.getId(), category));
        logger.debug("Initializer: categories are loaded");

        idGenerator.set(1);
        List<Location> locations = kudaGOService.getLocations();
        locations.forEach(location -> locationStorage.save(idGenerator.getAndIncrement(), location));
        logger.debug("Initializer: locations are loaded");

        logger.info("Initializer and method 'run' are finished");
    }
}
