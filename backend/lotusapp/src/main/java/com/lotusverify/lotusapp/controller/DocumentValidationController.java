package com.lotusverify.lotusapp.controller;

import com.lotusverify.lotusapp.model.ValidationReport;
import com.lotusverify.lotusapp.service.DocumentValidationService;
import com.lotusverify.lotusapp.service.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/validation")
public class DocumentValidationController {

    @Autowired
    private DocumentValidationService documentValidationService;

    @Autowired
    private MetricsService metricsService;

    @PostMapping("/validate")
    public ResponseEntity<ValidationResponse> validateDocument(@RequestBody DocumentRequest request) {
        // Validar el documento
        ValidationReport report = documentValidationService.validateDocument(request.getDocumentText());

        // Obtener métricas globales
        Metrics metrics = new Metrics(
                metricsService.getTotalQueries(),
                metricsService.getSearchSuccessRate(),
                metricsService.getPrecisionRate(),
                metricsService.getAverageResponseTime(),
                metricsService.getAverageRelevancyScore()
        );

        // Combinar el reporte de validación y las métricas
        ValidationResponse response = new ValidationResponse(report, metrics);

        return ResponseEntity.ok(response);
    }

    public static class DocumentRequest {
        private String documentText;

        public String getDocumentText() {
            return documentText;
        }

        public void setDocumentText(String documentText) {
            this.documentText = documentText;
        }
    }

    public class ValidationResponse {
        private ValidationReport validationReport;
        private Metrics metrics;

        public ValidationResponse(ValidationReport validationReport, Metrics metrics) {
            this.validationReport = validationReport;
            this.metrics = metrics;
        }

        public ValidationReport getValidationReport() {
            return validationReport;
        }

        public Metrics getMetrics() {
            return metrics;
        }
    }

    public static class Metrics {
        private long totalQueries;
        private double searchSuccessRate;
        private double precisionRate;
        private double averageResponseTime;
        private double averageRelevancyScore;

        public Metrics(long totalQueries, double searchSuccessRate, double precisionRate, double averageResponseTime, double averageRelevancyScore) {
            this.totalQueries = totalQueries;
            this.searchSuccessRate = searchSuccessRate;
            this.precisionRate = precisionRate;
            this.averageResponseTime = averageResponseTime;
            this.averageRelevancyScore = averageRelevancyScore;
        }

        public long getTotalQueries() {
            return totalQueries;
        }

        public double getSearchSuccessRate() {
            return searchSuccessRate;
        }

        public double getPrecisionRate() {
            return precisionRate;
        }

        public double getAverageResponseTime() {
            return averageResponseTime;
        }

        public double getAverageRelevancyScore() {
            return averageRelevancyScore;
        }
    }
}
