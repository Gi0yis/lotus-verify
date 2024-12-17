package com.lotusverify.lotusapp.controller;

import com.lotusverify.lotusapp.service.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MetricsController {

    @Autowired
    private MetricsService metricsService;

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        try {
            Map<String, Object> metrics = new HashMap<>();

            metrics.put("totalQueries", metricsService.getTotalQueries());
            metrics.put("percentageWithResults", metricsService.getSearchSuccessRate());
            metrics.put("averageResponseTime", metricsService.getAverageResponseTime());
            metrics.put("precisionRate", metricsService.getPrecisionRate());
            metrics.put("averageRelevancyScore", metricsService.getAverageRelevancyScore());

            return ResponseEntity.ok(metrics);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Ocurrió un error al recuperar las métricas: " + e.getMessage()));
        }
    }
}
