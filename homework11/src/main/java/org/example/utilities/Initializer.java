package org.example.utilities;

import lombok.RequiredArgsConstructor;
import org.example.annotations.LogExecutionTime;
import org.example.interfaces.AbstractInitializer;
import org.example.interfaces.DataObserver;
import org.example.interfaces.DataSubject;
import org.example.services.KudaGOService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

@Component
@RequiredArgsConstructor
public class Initializer extends AbstractInitializer implements DataSubject<Object> {
    private final KudaGOService kudaGOService;
    private final ExecutorService fixedThreadPool;
    private final ScheduledExecutorService scheduledThreadPool;

    private final List<DataObserver<Object>> observers = new CopyOnWriteArrayList<>();
    private final Logger logger = LoggerFactory.getLogger(Initializer.class);


    @LogExecutionTime
    @EventListener(ContextRefreshedEvent.class)
    public void initializeData() {
        logger.info("Initializer and method 'initializeData' are started");

        List<Callable<Void>> tasks = List.of(
                () -> {
                    initializeCategory.execute();
                    logger.debug("Initializer: categories are loaded");
                    return null;
                },
                () -> {
                    initializeLocation.execute();
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

