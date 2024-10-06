package com.example.tinkoff.utilities;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends Exception {
    private final Class<?> className;

    public EntityNotFoundException(Class<?> className) {
        this.className = className;
    }
}
