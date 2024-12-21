package com.lotusverify.lotusapp.controller;

import com.lotusverify.lotusapp.model.DocumentRequest;
import com.lotusverify.lotusapp.model.Metrics;
import com.lotusverify.lotusapp.model.ValidationReport;
import com.lotusverify.lotusapp.model.ValidationResponse;
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
        ValidationReport report = documentValidationService.validateDocument(request.getDocumentText());

        Metrics metrics = new Metrics(
                metricsService.getTotalQueries(),
                metricsService.getSearchSuccessRate(),
                metricsService.getPrecisionRate(),
                metricsService.getAverageResponseTime(),
                metricsService.getAverageRelevancyScore(),
                metricsService.getTruePositivesCount(),
                metricsService.getFalsePositivesCount(),
                metricsService.getTrueNegativesCount(),
                metricsService.getFalseNegativesCount(),
                metricsService.getFalsePositiveRate(),
                metricsService.getFalseNegativeRate(),
                metricsService.getAccuracyRate(),
                metricsService.getF1Score()
        );
        ValidationResponse response = new ValidationResponse(report, metrics);

        return ResponseEntity.ok(response);
    }
}