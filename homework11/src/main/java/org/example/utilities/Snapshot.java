package org.example.utilities;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Snapshot<T> {
    private final long id;
    private final T value;
    private final LocalDateTime time;

    public Snapshot(long id, T value) {
        this.id = id;
        this.value = value;
        this.time = LocalDateTime.now();
    }
}
