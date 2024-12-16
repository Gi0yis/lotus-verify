package com.lotusverify.lotusapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DocumentValidationService {

    @Autowired
    private TextAnalyticsService textAnalyticsService;

    @Autowired
    private BingSearchService bingSearchService;

    @Autowired
    private Gpt4oMiniService gpt4oMiniService;

    @Autowired
    private MetricsService metricsService;

    public String validateDocument(String documentText) {
        var report = new StringBuilder();

        List<Map<String, String>> entities = textAnalyticsService.extractEntities(documentText);
        report.append("=== Entidades Detectadas ===\n");
        report.append("Total: ").append(entities.size()).append("\n\n");

        int processedRequests = 0;
        int validCount = 0;

        for (Map<String, String> entity : entities) {
            if (processedRequests >= 10) {
                try {
                    Thread.sleep(60000);
                    processedRequests = 0;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Error al pausar solicitudes", e);
                }
            }

            var entityText = entity.get("Entity");
            report.append("Validando afirmación: ").append(entityText).append("\n");

            long searchStartTime = System.currentTimeMillis();
            var searchResults = bingSearchService.search(entityText);
            long responseTime = System.currentTimeMillis() - searchStartTime;

            var searchSuccess = !searchResults.contains("No se encontraron resultados");
            metricsService.logSearch(searchSuccess, responseTime);

            if (searchSuccess) {
                report.append("Resultados resumidos:\n").append(searchResults).append("\n");

                var prompt = "Valida si esta información es precisa: " + entityText +
                        "\nBasado en: " + searchResults;
                var validationResponse = gpt4oMiniService.getChatCompletion(prompt);

                boolean isPrecise = validationResponse.toLowerCase().contains("preciso") ||
                        validationResponse.toLowerCase().contains("correcto") ||
                        validationResponse.toLowerCase().contains("cierto");

                if (isPrecise) {
                    validCount++;
                }

                metricsService.logValidation(isPrecise);

                report.append("Resultado de validación: ").append(validationResponse).append("\n\n");
                processedRequests++;
            } else {
                report.append("No se encontraron resultados relevantes para la entidad.\n\n");
            }
        }

        report.append("\n=== Métricas del Proceso ===\n");
        report.append("Total de búsquedas realizadas: ").append(metricsService.getTotalQueries()).append("\n");
        report.append("Porcentaje de búsquedas exitosas: ")
                .append(String.format("%.2f", metricsService.getSearchSuccessRate())).append("%\n");
        report.append("Tiempo promedio de respuesta: ")
                .append(String.format("%.2f", metricsService.getAverageResponseTime())).append(" ms\n");
        report.append("Afirmaciones validadas: ").append(validCount).append("\n");
        report.append("Porcentaje de precisión: ")
                .append(String.format("%.2f", (double) validCount / entities.size() * 100)).append("%\n");

        return report.toString();
    }
}