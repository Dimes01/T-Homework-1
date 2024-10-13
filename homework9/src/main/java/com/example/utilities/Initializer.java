package com.example.utilities;

import org.example.annotations.LogExecutionTime;
import com.example.models.Category;
import com.example.models.Location;
import com.example.services.KudaGOService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class Initializer {

    private final KudaGOService kudaGOService;
    private final Storage<Category> categoryStorage;
    private final Storage<Location> locationStorage;
    private final Logger logger = LoggerFactory.getLogger(Initializer.class);
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final ExecutorService fixedThreadPool;
    private final ScheduledExecutorService scheduledThreadPool;
    private final Duration scheduleDuration;

    @Autowired
    public Initializer(
            KudaGOService kudaGOService,
            Storage<Category> categoryStorage,
            Storage<Location> locationStorage,
            @Qualifier("fixedThreadPool") ExecutorService fixedThreadPool,
            @Qualifier("scheduledThreadPool") ScheduledExecutorService scheduledThreadPool,
            @Value("${executor.schedule-duration}") Duration scheduleDuration) {
        this.kudaGOService = kudaGOService;
        this.categoryStorage = categoryStorage;
        this.locationStorage = locationStorage;
        this.fixedThreadPool = fixedThreadPool;
        this.scheduledThreadPool = scheduledThreadPool;
        this.scheduleDuration = scheduleDuration;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        scheduledThreadPool.scheduleAtFixedRate(this::initializeData, 0, scheduleDuration.toMillis(), TimeUnit.MILLISECONDS);
    }

    @LogExecutionTime
    public void initializeData() {
        logger.info("Initializer and method 'initializeData' are started");

        Future<?> categoriesFuture = fixedThreadPool.submit(() -> {
            List<Category> categories = kudaGOService.getCategories();
            categories.forEach(category -> categoryStorage.save(category.getId(), category));
            logger.debug("Initializer: categories are loaded");
        });

        Future<?> locationsFuture = fixedThreadPool.submit(() -> {
            idGenerator.set(1);
            List<Location> locations = kudaGOService.getLocations();
            locations.forEach(location -> locationStorage.save(idGenerator.getAndIncrement(), location));
            logger.debug("Initializer: locations are loaded");
        });

        try {
            categoriesFuture.get();
            locationsFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error occurred during initialization", e);
            Thread.currentThread().interrupt();
        }

        logger.info("Initializer and method 'initializeData' are finished");
    }
}
