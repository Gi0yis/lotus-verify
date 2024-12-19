package com.lotusverify.lotusapp.model;

import java.util.ArrayList;
import java.util.List;

public class ValidationReport {

    private int totalPhrases;
    private long executionTime;
    private final List<PhraseResult> results = new ArrayList<>();
    private final List<UnvalidatedPhrase> unvalidatedPhrases = new ArrayList<>(); // Nueva lista para frases no validadas

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

    public List<PhraseResult> getResults() {
        return results;
    }

    public void addPhraseResult(String phrase, String searchResults, String validation, boolean precise) {
        this.results.add(new PhraseResult(phrase, searchResults, validation, precise));
    }

    // Nuevo método para agregar frases no validadas
    public void addUnvalidatedPhrase(String phrase, String reason) {
        this.unvalidatedPhrases.add(new UnvalidatedPhrase(phrase, reason));
    }

    public List<UnvalidatedPhrase> getUnvalidatedPhrases() {
        return unvalidatedPhrases;
    }

    // Clases internas para representar los resultados de frases no validadas
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
}