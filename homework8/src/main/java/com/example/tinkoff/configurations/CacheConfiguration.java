package com.example.tinkoff.configurations;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfiguration {
    private final CacheManager cacheManager = new ConcurrentMapCacheManager("currenciesCursesByDate", "valuteInfoByISOCharCode");

    @Bean
    public CacheManager cacheManager() {
        return cacheManager;
    }

    @Scheduled(fixedRate = 3600000) // 1 час в миллисекундах
    public void clearCaches() {
        Objects.requireNonNull(cacheManager.getCache("currenciesCursesByDate")).clear();
        Objects.requireNonNull(cacheManager.getCache("valuteInfoByISOCharCode")).clear();
    }
}
