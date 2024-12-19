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

    @Autowired
    private DiffbotService diffbotService;

    @Autowired
    private TextAnalyticsService textAnalyticsService;

    public ValidationReport validateDocument(String documentText) {
        ValidationReport report = new ValidationReport();
        long startTime = System.currentTimeMillis();

        // Extraer frases clave del documento
        List<String> phrases = extractPhrasesService.extractRelevantPhrases(documentText);
        report.setTotalPhrases(phrases.size());

        int processedRequests = 0;
        long nextResetTime = System.currentTimeMillis() + TIME_WINDOW * 1000;

        for (String phrase : phrases) {
            if (processedRequests >= REQUEST_LIMIT) {
                long currentTime = System.currentTimeMillis();
                if (currentTime < nextResetTime) {
                    waitForRateLimitReset(nextResetTime - currentTime);
                }
                processedRequests = 0;
                nextResetTime = System.currentTimeMillis() + TIME_WINDOW * 1000;
            }

            String searchResults = bingSearchService.search(phrase);
            boolean isSearchSuccessful = !searchResults.contains("No se encontraron");
            metricsService.logSearch(isSearchSuccessful, System.currentTimeMillis() - startTime);

            if (!isSearchSuccessful) {
                continue;
            }

            String[] resultsArray = searchResults.split("\n\n");

            for (String resultText : resultsArray) {
                String pageUrl = BingSearchService.extractUrl(resultText);
                if (pageUrl.isEmpty()) {
                    continue;
                }

                String validationResponse = validatePhrase(phrase, resultText);
                boolean isPrecise = validationResponse.toLowerCase().contains("true");
                Boolean isDiffbotValid = validateWithDiffbotAndTextAnalyticsSafely(phrase, pageUrl);
                boolean isGeneratedAssertionCorrect = isDiffbotValid != null ? isPrecise && isDiffbotValid : isPrecise;

                boolean isHallucination = detectHallucinations(phrase, List.of(resultText));
                metricsService.logValidation(isGeneratedAssertionCorrect);
                metricsService.logValidationResult(isGeneratedAssertionCorrect, !isHallucination);

                report.addPhraseResult(phrase, resultText, validationResponse, isGeneratedAssertionCorrect);
            }

            processedRequests++;
        }

        long totalExecutionTime = System.currentTimeMillis() - startTime;
        report.setExecutionTime(totalExecutionTime);
        return report;
    }

    private boolean detectHallucinations(String generatedPhrase, List<String> trustedSources) {
        for (String source : trustedSources) {
            double similarityScore = bingSearchService.calculateRelevancy(generatedPhrase, source);
            if (similarityScore > 0.8) {
                return false;
            }
        }
        return true;
    }

    private void waitForRateLimitReset(long waitTimeMillis) {
        try {
            TimeUnit.MILLISECONDS.sleep(waitTimeMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String validatePhrase(String phrase, String searchResult) {
        String prompt = "Valida si esta información es precisa: " + phrase +
                "\nBasado en: " + searchResult;
        return validatePhrasesService.getChatCompletion(prompt);
    }

    private Boolean validateWithDiffbotAndTextAnalyticsSafely(String phrase, String pageUrl) {
        try {
            return validateWithDiffbotAndTextAnalytics(phrase, pageUrl);
        } catch (Exception e) {
            System.err.println("Error al validar con Diffbot y TextAnalytics: " + e.getMessage());
            return null;
        }
    }

    private Boolean validateWithDiffbotAndTextAnalytics(String phrase, String pageUrl) {
        try {
            String extractedText = diffbotService.extractTextFromUrl(pageUrl);
            if (extractedText == null || extractedText.isEmpty()) {
                return false;
            }

            List<String> pageKeyPhrases = textAnalyticsService.extractKeyPhrases(extractedText);
            List<String> inputKeyPhrases = textAnalyticsService.extractKeyPhrases(phrase);

            for (String keyPhrase : inputKeyPhrases) {
                if (pageKeyPhrases.contains(keyPhrase)) {
                    return true;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al validar con Diffbot y TextAnalytics: " + e.getMessage());
        }
        return false;
    }
}