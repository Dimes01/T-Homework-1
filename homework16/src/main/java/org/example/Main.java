package org.example;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args) {
        log.trace("Some trace message");
        log.debug("Some debug message");
        log.info("Some info message");
        log.warn("Some warn message");
        log.error("Some error message");
    }
}
