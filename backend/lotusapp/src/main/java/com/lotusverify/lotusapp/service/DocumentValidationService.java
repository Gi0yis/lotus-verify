package com.lotusverify.lotusapp.service;

import com.lotusverify.lotusapp.model.ValidationReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DocumentValidationService {

    private static final int REQUEST_LIMIT = 6;
    private static final long TIME_WINDOW = 60;

    @Autowired
    private ExtractPhrasesService extractPhrasesService;

    @Autowired
    private BingSearchService bingSearchService;

    @Autowired
    private ValidatePhrasesService validatePhrasesService;

    @Autowired
    private MetricsService metricsService;

    public ValidationReport validateDocument(String documentText) {
        ValidationReport report = new ValidationReport();
        long startTime = System.currentTimeMillis();

        List<String> phrases = extractPhrasesService.extractRelevantPhrases(documentText);
        report.setTotalPhrases(phrases.size());

        var processedRequests = 0;
        for (String phrase : phrases) {
            if (processedRequests >= REQUEST_LIMIT) {
                waitForRateLimitReset();
                processedRequests = 0;
            }

            String searchResults = bingSearchService.search(phrase);
            metricsService.logSearch(!searchResults.contains("No se encontraron"), System.currentTimeMillis() - startTime);

            String validationResponse = validatePhrase(phrase, searchResults);
            var isPrecise = validationResponse.toLowerCase().contains("preciso") ||
                    validationResponse.toLowerCase().contains("cierto");

            report.addPhraseResult(phrase, searchResults, validationResponse, isPrecise);
            metricsService.logValidation(isPrecise);

            processedRequests++;
        }

        long totalExecutionTime = System.currentTimeMillis() - startTime;
        report.setExecutionTime(totalExecutionTime);
        return report;
    }

    private void waitForRateLimitReset() {
        try {
            System.out.println("Esperando para evitar límites de tasa...");
            TimeUnit.SECONDS.sleep(TIME_WINDOW);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String validatePhrase(String phrase, String searchResults) {
        var prompt = "Valida si esta información es precisa: " + phrase +
                "\nBasado en: " + searchResults;
        return validatePhrasesService.getChatCompletion(prompt);
    }
}
