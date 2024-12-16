package com.lotusverify.lotusapp.service;

import com.lotusverify.lotusapp.model.SearchResult;
import com.lotusverify.lotusapp.repository.ISearchResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MetricsService {

    @Autowired
    private ISearchResultRepository searchResultRepository;

    private long totalAssertionsValidated = 0;
    private long preciseAssertions = 0;
    private long successfulSearches = 0;
    private long totalSearchTime = 0; // en milisegundos

    // Incrementar métricas de validación
    public void logValidation(boolean isPrecise) {
        totalAssertionsValidated++;
        if (isPrecise) preciseAssertions++;
    }

    // Incrementar búsquedas exitosas
    public void logSearch(boolean isSuccess, long responseTime) {
        if (isSuccess) successfulSearches++;
        totalSearchTime += responseTime;
    }

    // Métricas generales
    public long getTotalQueries() {
        return searchResultRepository.count();
    }

    public double getPercentageWithResults() {
        List<SearchResult> results = searchResultRepository.findAll();

        var withResults = results.stream().filter(r -> r.getResult() != null && !r.getResult().isEmpty()).count();
        return ((double) withResults / results.size() * 100);
    }

    public double getAverageResponseTime() {
        return totalSearchTime > 0 ? (double) totalSearchTime / getTotalQueries() : 0;
    }

    public long getQueriesByDay(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        return searchResultRepository.findAll().stream()
                .filter(r -> r.getTimestamp().isAfter(start) && r.getTimestamp().isBefore(end))
                .count();
    }

    // Métricas específicas del proceso de validación
    public long getTotalAssertionsValidated() {
        return totalAssertionsValidated;
    }

    public double getPrecisionRate() {
        return totalAssertionsValidated > 0
                ? ((double) preciseAssertions / totalAssertionsValidated) * 100
                : 0;
    }

    public double getSearchSuccessRate() {
        return getTotalQueries() > 0
                ? ((double) successfulSearches / getTotalQueries()) * 100
                : 0;
    }
}