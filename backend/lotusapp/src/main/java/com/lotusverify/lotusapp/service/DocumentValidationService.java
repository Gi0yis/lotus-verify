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

        // Extraer frases clave del documento
        List<String> phrases = extractPhrasesService.extractRelevantPhrases(documentText);
        report.setTotalPhrases(phrases.size());

        int processedRequests = 0;

        for (String phrase : phrases) {
            if (processedRequests >= REQUEST_LIMIT) {
                waitForRateLimitReset();
                processedRequests = 0;
            }

            // Buscar información en VimSearch
            String searchResults = bingSearchService.search(phrase);
            boolean isSearchSuccessful = !searchResults.contains("No se encontraron");
            metricsService.logSearch(isSearchSuccessful, System.currentTimeMillis() - startTime);

            if (!isSearchSuccessful) {
                continue;
            }

            String[] pages = searchResults.split("\n\n");
            int relevancyScore = 0;

            for (String page : pages) {
                String validationResponse = validatePhrase(phrase, page);
                boolean isPrecise = validationResponse.toLowerCase().contains("true");

                report.addPhraseResult(phrase, page, validationResponse, isPrecise);
                metricsService.logValidation(isPrecise);

                boolean isGeneratedAssertionCorrect = isPrecise;
                boolean isValidatedAsCorrect = isPrecise;
                metricsService.logValidationResult(isGeneratedAssertionCorrect, isValidatedAsCorrect);

                // Calcular relevancia
                relevancyScore += bingSearchService.calculateRelevancy(phrase, page);
            }

            metricsService.logRelevancyScore(relevancyScore);
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

    private String validatePhrase(String phrase, String searchResult) {
        String prompt = "Valida si esta información es precisa: " + phrase +
                "\nBasado en: " + searchResult;
        return validatePhrasesService.getChatCompletion(prompt);
    }
}