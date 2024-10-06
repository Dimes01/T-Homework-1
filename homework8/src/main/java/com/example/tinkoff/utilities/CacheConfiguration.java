package com.example.tinkoff.utilities;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfiguration {
    @Bean
    public CacheManager cacheManager() {
        var cacheManager = new ConcurrentMapCacheManager("currenciesCursesByDate", "valuteInfoByISOCharCode");
        var executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            cacheManager.getCache("currenciesCursesByDate").clear();
            cacheManager.getCache("valuteInfoByISOCharCode").clear();
        }, 1, 1, TimeUnit.HOURS);
        return cacheManager;
    }
}
