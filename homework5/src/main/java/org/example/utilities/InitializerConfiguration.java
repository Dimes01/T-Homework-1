package org.example.utilities;

import org.example.homework5.models.Category;
import org.example.homework5.models.Location;
import org.example.services.KudaGOService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class InitializerConfiguration {
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Bean
    public Initializer initializer(
            KudaGOService kudaGOService,
            Storage<Category> categoryStorage,
            Storage<Location> locationStorage,
            @Qualifier("processingOfInitialize") ExecutorService fixedThreadPool,
            @Qualifier("scheduleInitialize") ScheduledExecutorService scheduledThreadPool,
            @Value("${executor.schedule-duration}") Duration scheduleDuration) {

        Initializer initializer = new Initializer(
                kudaGOService,
                fixedThreadPool,
                scheduledThreadPool
        );

        initializer.initializeCategory = () -> {
            List<Category> categories = kudaGOService.getCategories();
            categories.forEach(category -> initializer.notifyObservers(category.getId(), category));
        };
        initializer.initializeLocation = () -> {
            List<Location> locations = kudaGOService.getLocations();
            locations.forEach(location -> initializer.notifyObservers(idGenerator.getAndIncrement(), location));
        }

        scheduledThreadPool.scheduleAtFixedRate(initializer::initializeData, 0, scheduleDuration.toHours(), TimeUnit.HOURS);

        initializer.addObserver(categoryStorage);
        initializer.addObserver(locationStorage);

        return initializer;
    }
}
