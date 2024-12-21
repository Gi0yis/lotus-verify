package com.lotusverify.lotusapp.model;

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
