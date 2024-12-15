package com.lotusverify.lotusapp.controller;

import com.lotusverify.lotusapp.service.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MetricsController {

    @Autowired
    private MetricsService metricsService;

    @GetMapping("/metrics")
    public Map<String, Object> getMetrics(@RequestParam(required = false) String date) {

        Map<String, Object> metrics = new HashMap<>();

        metrics.put("totalQueries", metricsService.getTotalQueries());
        metrics.put("percentageWithResults", metricsService.getPercentageWithResults());
        metrics.put("averageResponseTime", metricsService.getAverageResponseTime());

        if (date != null) {
            LocalDate localDate = LocalDate.parse(date);
            metrics.put("queriesByDay", metricsService.getQueriesByDay(localDate));
        }

        return metrics;
    }
}
