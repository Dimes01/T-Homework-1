package com.example.utilities;

import com.example.models.Category;
import com.example.models.Location;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class StorageConfiguration {
    private final Storage<Category> categoryStorage = new Storage<>();
    private final Storage<Location> locationStorage = new Storage<>();

    @Bean
    public Storage<Category> getCategoryStorage() {
        return categoryStorage;
    }

    @Bean
    public Storage<Location> getLocationStorage() {
        return locationStorage;
    }
}


