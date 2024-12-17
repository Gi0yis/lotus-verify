package com.lotusverify.lotusapp.model;

import java.util.ArrayList;
import java.util.List;

public class ValidationReport {

    private int totalPhrases;
    private long executionTime;
    private final List<PhraseResult> results = new ArrayList<>();

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
}