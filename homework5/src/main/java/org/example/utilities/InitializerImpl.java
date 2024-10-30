package org.example.utilities;

import lombok.RequiredArgsConstructor;
import org.example.annotations.LogExecutionTime;
import org.example.homework5.models.Category;
import org.example.homework5.models.Location;
import org.example.interfaces.DataObserver;
import org.example.interfaces.DataSubject;
import org.example.interfaces.Initializer;
import org.example.services.KudaGOService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
public class InitializerImpl {
    private final ExecutorService fixedThreadPool;
    private final ScheduledExecutorService scheduledThreadPool;
    private final Initializer initializer;

    private final Logger logger = LoggerFactory.getLogger(InitializerImpl.class);


    @LogExecutionTime
    @EventListener(ContextRefreshedEvent.class)
    public void initializeData() {
        logger.info("InitializerImpl and method 'initializeData' are started");

        List<Callable<Void>> tasks = List.of(
                () -> {
                    initializer.initializeCategory.execute();
                    return null;
                },
                () -> {
                    initializer.initializeLocation.execute();
                    return null;
                }
        );

        try {
            fixedThreadPool.invokeAll(tasks);
        } catch (InterruptedException e) {
            logger.error("Initialization interrupted", e);
            Thread.currentThread().interrupt();
        }

        logger.info("InitializerImpl and method 'initializeData' are finished");
    }
}

