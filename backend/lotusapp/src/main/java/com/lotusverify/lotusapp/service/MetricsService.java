package com.lotusverify.lotusapp.service;


import com.lotusverify.lotusapp.repository.ISearchResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    @Autowired
    private ISearchResultRepository searchResultRepository;

    private long totalAssertionsValidated = 0;
    private long preciseAssertions = 0;
    private long successfulSearches = 0;
    private long totalSearchTime = 0;
    private long totalRelevancyScore = 0;
    private long relevancyCount = 0;

    public void logValidation(boolean isPrecise) {
        totalAssertionsValidated++;
        if (isPrecise) preciseAssertions++;
    }

    public void logSearch(boolean isSuccess, long responseTime) {
        if (isSuccess) successfulSearches++;
        totalSearchTime += responseTime;
    }

    public void logRelevancyScore(int score) {
        totalRelevancyScore += score;
        relevancyCount++;
    }

    public double getAverageRelevancyScore() {
        return relevancyCount > 0 ? (double) totalRelevancyScore / relevancyCount : 0;
    }

    public long getTotalQueries() {
        return searchResultRepository.count();
    }

    public double getSearchSuccessRate() {
        return getTotalQueries() > 0
                ? ((double) successfulSearches / getTotalQueries()) * 100
                : 0;
    }

    public double getPrecisionRate() {
        return totalAssertionsValidated > 0
                ? ((double) preciseAssertions / totalAssertionsValidated) * 100
                : 0;
    }

    public double getAverageResponseTime() {
        return totalSearchTime > 0 ? (double) totalSearchTime / getTotalQueries() : 0;
    }
}
