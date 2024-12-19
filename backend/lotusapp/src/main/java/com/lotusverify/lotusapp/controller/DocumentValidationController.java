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
        // Validar el documento y generar el reporte
        ValidationReport report = documentValidationService.validateDocument(request.getDocumentText());

        // Crear objeto de métricas con los valores actualizados
        Metrics metrics = new Metrics(
                metricsService.getTotalQueries(),
                metricsService.getSearchSuccessRate(),
                metricsService.getPrecisionRate(),
                metricsService.getAverageResponseTime(),
                metricsService.getAverageRelevancyScore(),
                metricsService.getTruePositives(),
                metricsService.getFalsePositives(),
                metricsService.getTrueNegatives(),
                metricsService.getFalseNegatives(),
                metricsService.getFalsePositiveRate(),
                metricsService.getFalseNegativeRate()
        );

        // Crear respuesta con el reporte y las métricas
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

    public static class ValidationResponse {
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
        private long truePositives;
        private long falsePositives;
        private long trueNegatives;
        private long falseNegatives;
        private double falsePositiveRate;
        private double falseNegativeRate;

        public Metrics(long totalQueries, double searchSuccessRate, double precisionRate, double averageResponseTime,
                       double averageRelevancyScore, long truePositives, long falsePositives, long trueNegatives,
                       long falseNegatives, double falsePositiveRate, double falseNegativeRate) {
            this.totalQueries = totalQueries;
            this.searchSuccessRate = searchSuccessRate;
            this.precisionRate = precisionRate;
            this.averageResponseTime = averageResponseTime;
            this.averageRelevancyScore = averageRelevancyScore;
            this.truePositives = truePositives;
            this.falsePositives = falsePositives;
            this.trueNegatives = trueNegatives;
            this.falseNegatives = falseNegatives;
            this.falsePositiveRate = falsePositiveRate;
            this.falseNegativeRate = falseNegativeRate;
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

        public long getTruePositives() {
            return truePositives;
        }

        public long getFalsePositives() {
            return falsePositives;
        }

        public long getTrueNegatives() {
            return trueNegatives;
        }

        public long getFalseNegatives() {
            return falseNegatives;
        }

        public double getFalsePositiveRate() {
            return falsePositiveRate;
        }

        public double getFalseNegativeRate() {
            return falseNegativeRate;
        }
    }
}