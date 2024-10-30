package org.example.utilities;

import lombok.RequiredArgsConstructor;
import org.example.annotations.LogExecutionTime;
import org.example.homework5.models.Category;
import org.example.homework5.models.Location;
import org.example.interfaces.DataObserver;
import org.example.interfaces.DataSubject;
import org.example.services.KudaGOService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
public class Initializer implements DataSubject<Object> {
    private final KudaGOService kudaGOService;
    private final ExecutorService fixedThreadPool;
    private final ScheduledExecutorService scheduledThreadPool;

    private final List<DataObserver<Object>> observers = new CopyOnWriteArrayList<>();
    private final Logger logger = LoggerFactory.getLogger(Initializer.class);
    private final AtomicLong idGenerator = new AtomicLong(1);


    @LogExecutionTime
    @EventListener(ContextRefreshedEvent.class)
    public void initializeData() {
        logger.info("Initializer and method 'initializeData' are started");

        List<Callable<Void>> tasks = List.of(
                () -> {
                    List<Category> categories = kudaGOService.getCategories();
                    categories.forEach(category -> notifyObservers(category.getId(), category));
                    logger.debug("Initializer: categories are loaded");
                    return null;
                },
                () -> {
                    List<Location> locations = kudaGOService.getLocations();
                    locations.forEach(location -> notifyObservers(idGenerator.getAndIncrement(), location));
                    logger.debug("Initializer: locations are loaded");
                    return null;
                }
        );

        try {
            fixedThreadPool.invokeAll(tasks);
        } catch (InterruptedException e) {
            logger.error("Initialization interrupted", e);
            Thread.currentThread().interrupt();
        }

        logger.info("Initializer and method 'initializeData' are finished");
    }

    @Override
    public void addObserver(DataObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(DataObserver<Object> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Long id, Object data) {
        observers.forEach(observer -> observer.updateData(id, data));
    }
}

