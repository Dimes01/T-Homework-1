package com.example.tinkoff.configurations;

import com.example.tinkoff.utilities.CurrencyException;
import com.example.tinkoff.utilities.CurrencyNotExistException;
import com.example.tinkoff.utilities.CurrencyNotFoundException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CircuitBreakerConfiguration {
    @Bean
    public CircuitBreaker circuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .slidingWindowSize(100)
                .permittedNumberOfCallsInHalfOpenState(10)
                .slowCallDurationThreshold(Duration.ofSeconds(4))
                .slowCallRateThreshold(90)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .minimumNumberOfCalls(10)
                .ignoreExceptions(
                        CurrencyException.class,
                        CurrencyNotExistException.class,
                        CurrencyNotFoundException.class
                )
                .build();

        return registry.circuitBreaker("myCircuitBreaker", circuitBreakerConfig);
    }
}
