package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetricsController {

    @Autowired
    private CustomMetricsService customMetrics;

    @GetMapping("/increment")
    public String incrementCounter() {
        customMetrics.incrementCustomMetric();
        return "Counter incremented";
    }
}
