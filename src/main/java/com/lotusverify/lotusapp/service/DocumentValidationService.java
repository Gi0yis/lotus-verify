package com.lotusverify.lotusapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lotusverify.lotusapp.model.ValidationReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class DocumentValidationService {

    private static final int REQUEST_LIMIT = 6;
    private static final long TIME_WINDOW = 60;
    private final ExecutorService executorService = Executors.newFixedThreadPool(REQUEST_LIMIT);

    @Autowired
    private ExtractPhrasesService extractPhrasesService;

    @Autowired
    private BingSearchService bingSearchService;

    @Autowired
    private ValidatePhrasesService validatePhrasesService;

    @Autowired
    private MetricsService metricsService;

    @Autowired
    private DiffbotService diffbotService;

    @Autowired
    private TextAnalyticsService textAnalyticsService;

    public ValidationReport validateDocument(String documentText) {
        ValidationReport report = new ValidationReport();
        long startTime = System.currentTimeMillis();

        List<String> phrases = extractPhrasesService.extractRelevantPhrases(documentText);
        report.setTotalPhrases(phrases.size());

        CompletableFuture<?>[] validationTasks = phrases.stream()
                .map(phrase -> CompletableFuture.runAsync(() -> processPhrase(phrase, report), executorService))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(validationTasks).join();
        long totalExecutionTime = System.currentTimeMillis() - startTime;

        report.setExecutionTime(totalExecutionTime);
        report.setPrecisionRate(metricsService.getPrecisionRate());
        report.setSearchSuccessRate(metricsService.getSearchSuccessRate());
        report.setHighRelevancyPercentage(metricsService.getHighRelevancyPercentage());
        report.setF1Score(metricsService.getF1Score());
        report.setAccuracyRate(metricsService.getAccuracyRate());

        executorService.shutdown();

        return report;
    }

    private void processPhrase(String phrase, ValidationReport report) {
        try {
            String searchResults = bingSearchService.search(phrase);
            boolean isSearchSuccessful = !searchResults.contains("No se encontraron");
            metricsService.logSearch(isSearchSuccessful, System.currentTimeMillis());

            if (!isSearchSuccessful) return;

            String[] resultsArray = searchResults.split("\n\n");
            for (String resultText : resultsArray) {
                String pageUrl = BingSearchService.extractUrl(resultText);
                if (pageUrl.isEmpty()) continue;

                boolean isGeneratedAssertionCorrect = validatePhrase(phrase, resultText, pageUrl);
                report.addPhraseResult(phrase, resultText, isGeneratedAssertionCorrect, isGeneratedAssertionCorrect);
            }
        } catch (Exception e) {
            System.err.println("Error al procesar la frase: " + phrase + ". " + e.getMessage());
        }
    }

    private boolean validatePhrase(String phrase, String resultText, String pageUrl) {
        String validationResponse = validatePhrasesService.getChatCompletion(
                "Valida si esta información es precisa: " + phrase + "\nBasado en: " + resultText
        );

        boolean isPrecise = validationResponse.toLowerCase().contains("correcto") || validationResponse.toLowerCase().contains("preciso");
        Boolean isDiffbotValid = validateWithDiffbotAndTextAnalyticsSafely(phrase, pageUrl);
        boolean isHallucination = detectHallucinations(phrase, List.of(resultText));

        metricsService.logValidation(isPrecise, !isHallucination);
        return isPrecise && (isDiffbotValid == null || !isHallucination);
    }

    private boolean detectHallucinations(String generatedPhrase, List<String> trustedSources) {
        return trustedSources.stream()
                .noneMatch(source -> bingSearchService.calculateNormalizedRelevancyScore(generatedPhrase, source) > 55.0);
    }

    private Boolean validateWithDiffbotAndTextAnalyticsSafely(String phrase, String pageUrl) {
        try {
            return validateWithDiffbotAndTextAnalytics(phrase, pageUrl);
        } catch (Exception e) {
            System.err.println("Error al validar con Diffbot y TextAnalytics: " + e.getMessage());
            return null;
        }
    }

    private Boolean validateWithDiffbotAndTextAnalytics(String phrase, String pageUrl) throws JsonProcessingException {
        String extractedText = diffbotService.extractTextFromUrl(pageUrl);
        if (extractedText == null || extractedText.isEmpty()) return false;

        List<String> pageKeyPhrases = textAnalyticsService.extractKeyPhrases(extractedText);
        List<String> inputKeyPhrases = textAnalyticsService.extractKeyPhrases(phrase);

        return inputKeyPhrases.stream().anyMatch(pageKeyPhrases::contains);
    }
}
