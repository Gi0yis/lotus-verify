package com.lotusverify.lotusapp.model;

public class PhraseResult {
    private String phrase;
    private String searchResults;
    private String validation;
    private boolean precise;

    public PhraseResult(String phrase, String searchResults, String validation, boolean precise) {
        this.phrase = phrase;
        this.searchResults = searchResults;
        this.validation = validation;
        this.precise = precise;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public String getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(String searchResults) {
        this.searchResults = searchResults;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public boolean isPrecise() {
        return precise;
    }

    public void setPrecise(boolean precise) {
        this.precise = precise;
    }
}
