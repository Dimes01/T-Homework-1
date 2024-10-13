package org.example.utilities;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class ExecutorConfiguration {

    @Value("${executor.fixed-thread-pool-size}")
    private int fixedThreadPoolSize;

    @Value("${executor.scheduled-thread-pool-size}")
    private int scheduledThreadPoolSize;


    @Bean("fixedThreadPool")
    public ExecutorService fixedThreadPool() {
        return Executors.newFixedThreadPool(fixedThreadPoolSize);
    }

    @Bean("scheduledThreadPool")
    public ScheduledExecutorService scheduledThreadPool() {
        return Executors.newScheduledThreadPool(scheduledThreadPoolSize);
    }
}
