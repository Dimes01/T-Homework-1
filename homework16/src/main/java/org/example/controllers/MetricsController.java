package org.example.controllers;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.RandomNumberDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@Slf4j
@RestController
public class MetricsController {
    private final Counter customMetricCounter;

    public MetricsController(MeterRegistry meterRegistry) {
        customMetricCounter = Counter.builder("my_metric_name")
            .description("Description of custom metric")
            .register(meterRegistry);
    }

    @PostMapping("/random")
    public ResponseEntity<String> randomNumberCounter(@RequestBody RandomNumberDTO dto) {
        customMetricCounter.increment();
        var number = generateRandomNumber(dto.getMin(), dto.getMax());
        String result;
        if (Arrays.stream(dto.getLoseNumbers()).anyMatch((x) -> x == number)) {
            result = String.format("The number is %d\nYou are lose!", number);
            log.warn("LOSE");
        } else {
            result = String.format("The number is %d\nYou are win!", number);
            log.info("WIN");
        }
        log.info(result);
        return ResponseEntity.ok(result);
    }

    private int generateRandomNumber(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }
}
