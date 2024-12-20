package com.lotusverify.lotusapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.util.concurrent.RateLimiter;
import com.lotusverify.lotusapp.model.ValidationReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;

@Service
public class DocumentValidationService {

    private final ExecutorService executorService = new ThreadPoolExecutor(
            5,
            5,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(50),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    private final BlockingQueue<Runnable> diffbotQueue = new LinkedBlockingQueue<>(100);

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

    private final RateLimiter rateLimiter = RateLimiter.create(0.08);

    public DocumentValidationService() {
        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                try {
                    diffbotQueue.take().run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

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
        report.setAverageRelevancyScore(metricsService.getAverageRelevancyScore());

        return report;
    }

    private void processPhrase(String phrase, ValidationReport report) {
        try {
            String searchResults = bingSearchService.search(phrase);
            rateLimiter.acquire();

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

    private Boolean validateWithDiffbotAndTextAnalyticsSafely(String phrase, String pageUrl) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        diffbotQueue.offer(() -> {
            try {
                Boolean result = retryWithExponentialBackoff(() -> validateWithDiffbotAndTextAnalytics(phrase, pageUrl));
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        try {
            return future.get();
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

    private Boolean retryWithExponentialBackoff(Callable<Boolean> task) {
        int retries = 0;
        long waitTime = 1000;

        while (retries < 5) {
            try {
                return task.call();
            } catch (Exception e) {
                retries++;
                System.err.println("Reintentando... Intento: " + retries);
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return null;
                }
                waitTime *= 3;
            }
        }
        System.err.println("Se excedieron los intentos máximos de reintento.");
        return null;
    }

    private boolean detectHallucinations(String generatedPhrase, List<String> trustedSources) {
        return trustedSources.stream()
                .noneMatch(source -> bingSearchService.calculateNormalizedRelevancyScore(generatedPhrase, source) > 55.0);
    }
}
