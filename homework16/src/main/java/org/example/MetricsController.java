package org.example;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetricsController {
    private final Counter customMetricCounter;

    public MetricsController(MeterRegistry meterRegistry) {
        customMetricCounter = Counter.builder("my_metric_name")
            .description("Description of custom metric")
            .register(meterRegistry);
    }

    @GetMapping("/increment")
    public String incrementCounter() {
        customMetricCounter.increment();
        return "Counter incremented: " + (int) customMetricCounter.count();
    }
}
