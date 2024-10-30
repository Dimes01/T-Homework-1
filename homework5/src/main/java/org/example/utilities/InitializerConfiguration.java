package org.example.utilities;

import org.example.homework5.models.Category;
import org.example.homework5.models.Location;
import org.example.interfaces.Initializer;
import org.example.services.KudaGOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class InitializerConfiguration {
    @Autowired private KudaGOService kudaGOService;
    @Autowired private Storage<Category> categoryStorage;
    @Autowired private Storage<Location> locationStorage;

    @Qualifier("processingOfInitialize")
    private ExecutorService fixedThreadPool;

    @Qualifier("scheduleInitialize")
    private ScheduledExecutorService scheduledThreadPool;

    @Value("${executor.schedule-duration}")
    Duration scheduleDuration;

    private final AtomicLong idGenerator = new AtomicLong(1);

    @Bean
    public Initializer initializer() {
        SimpleInitializer simpleInitializer = new SimpleInitializer();

        simpleInitializer.initializeCategory = () -> {
            List<Category> categories = kudaGOService.getCategories();
            categories.forEach(category -> simpleInitializer.notifyObservers(category.getId(), category));
        };
        simpleInitializer.initializeLocation = () -> {
            List<Location> locations = kudaGOService.getLocations();
            locations.forEach(location -> simpleInitializer.notifyObservers(idGenerator.getAndIncrement(), location));
        };

        simpleInitializer.addObserver(categoryStorage);
        simpleInitializer.addObserver(locationStorage);

        return simpleInitializer;
    }
}
