package com.lotusverify.lotusapp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ValidationReport {

    private int totalPhrases;
    private long executionTime;
    private double precisionRate;
    private double searchSuccessRate;
    private double averageRelevancyScore;
    private double f1Score;
    private double accuracyRate;
    private double highRelevancyPercentage;
    private final List<PhraseResult> results = new ArrayList<>();
    private final List<UnvalidatedPhrase> unvalidatedPhrases = new ArrayList<>();

    public int getTotalPhrases() {
        return totalPhrases;
    }

    public void setTotalPhrases(int totalPhrases) {
        this.totalPhrases = totalPhrases;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public double getPrecisionRate() {
        return precisionRate;
    }

    public void setPrecisionRate(double precisionRate) {
        this.precisionRate = precisionRate;
    }

    public double getSearchSuccessRate() {
        return searchSuccessRate;
    }

    public void setSearchSuccessRate(double searchSuccessRate) {
        this.searchSuccessRate = searchSuccessRate;
    }

    public double getAverageRelevancyScore() {
        return averageRelevancyScore;
    }

    public void setAverageRelevancyScore(double averageRelevancyScore) {
        this.averageRelevancyScore = averageRelevancyScore;
    }

    public double getF1Score() {
        return f1Score;
    }

    public void setF1Score(double f1Score) {
        this.f1Score = f1Score;
    }

    public double getAccuracyRate() {
        return accuracyRate;
    }

    public void setAccuracyRate(double accuracyRate) {
        this.accuracyRate = accuracyRate;
    }

    public List<PhraseResult> getResults() {
        return results;
    }

    public List<UnvalidatedPhrase> getUnvalidatedPhrases() {
        return unvalidatedPhrases;
    }

    public void addPhraseResult(String phrase, String searchResults, boolean validation, boolean precise) {
        this.results.add(new PhraseResult(phrase, searchResults, validation, precise));
    }

    public void addUnvalidatedPhrase(String phrase, String reason) {
        this.unvalidatedPhrases.add(new UnvalidatedPhrase(phrase, reason));
    }

    public int getValidatedPhrasesCount() {
        return (int) results.stream().filter(PhraseResult::isValidation).count();
    }

    public int getUnvalidatedPhrasesCount() {
        return unvalidatedPhrases.size();
    }

    public double calculatePrecision() {
        long truePositives = results.stream().filter(PhraseResult::isValidation).count();
        long totalValidated = results.size();
        return totalValidated > 0 ? (double) truePositives / totalValidated : 0.0;
    }

    public double calculateF1Score() {
        double precision = calculatePrecision();
        double recall = getValidatedPhrasesCount() / (double) totalPhrases;
        return (precision + recall) > 0 ? 2 * (precision * recall) / (precision + recall) : 0.0;
    }


    public void setHighRelevancyPercentage(double highRelevancyPercentage) {
        this.highRelevancyPercentage = highRelevancyPercentage;
    }

    public double getHighRelevancyPercentage() {
        return highRelevancyPercentage;
    }

    public String generateSummary() {
        return String.format(
                "Validation Report Summary:\n" +
                        "Total Phrases: %d\n" +
                        "Execution Time: %d ms\n" +
                        "Precision Rate: %.2f%%\n" +
                        "Search Success Rate: %.2f%%\n" +
                        "Average Relevancy Score: %.2f\n" +
                        "High Relevancy Percentage: %.2f%%\n" +
                        "F1 Score: %.2f\n" +
                        "Accuracy Rate: %.2f%%\n" +
                        "Validated Phrases: %d\n" +
                        "Unvalidated Phrases: %d\n",
                totalPhrases, executionTime, precisionRate, searchSuccessRate,
                averageRelevancyScore, highRelevancyPercentage, f1Score, accuracyRate,
                getValidatedPhrasesCount(), getUnvalidatedPhrasesCount()
        );

    }


    public static class UnvalidatedPhrase {
        private final String phrase;
        private final String reason;


        public UnvalidatedPhrase(String phrase, String reason) {
            this.phrase = phrase;
            this.reason = reason;
        }

        public String getPhrase() {
            return phrase;
        }

        public String getReason() {
            return reason;
        }
    }

    // Clase interna para resultados de frases
    public static class PhraseResult {
        private final String phrase;
        private final String searchResults;
        private final boolean validation;
        private final boolean precise;

        public PhraseResult(String phrase, String searchResults, boolean validation, boolean precise) {
            this.phrase = phrase;
            this.searchResults = searchResults;
            this.validation = validation;
            this.precise = precise;
        }

        public String getPhrase() {
            return phrase;
        }

        public String getSearchResults() {
            return searchResults;
        }

        public boolean isValidation() {
            return validation;
        }

        public boolean isPrecise() {
            return precise;
        }
    }
}