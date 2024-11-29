package org.example;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class CustomMetricsService {

    private final Counter customMetricCounter;

    public CustomMetricsService(MeterRegistry meterRegistry) {
        customMetricCounter = Counter.builder("my_metric_name")
            .description("Description of custom metric")
            .register(meterRegistry);
    }

    public void incrementCustomMetric() {
        customMetricCounter.increment();
    }
}