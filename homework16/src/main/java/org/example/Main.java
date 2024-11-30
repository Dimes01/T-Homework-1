package org.example;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        Task2(args);
        Task1();
//        triggerOutOfMemoryError();
//        triggerStackOverflowError();
    }

    private static void Task1() {
//        MDC.put("userId", "12345");
//        MDC.put("transactionId", "67890");

        log.info("Some info message 1");
        log.info("Some info message 2");
        log.info("Some info message 3");

//        MDC.clear();
    }

    private static void Task2(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    private static void triggerOutOfMemoryError() {
        try {
            long[] array = new long[Integer.MAX_VALUE];
        } catch (OutOfMemoryError e) {
            System.out.println("Caught OutOfMemoryError: " + e.getMessage());
        }
    }

    private static void triggerStackOverflowError() {
        try {
            recursiveMethod();
        } catch (StackOverflowError e) {
            System.out.println("Caught StackOverflowError: " + e.getMessage());
        }
    }

    private static void recursiveMethod() {
        recursiveMethod();
    }
}
