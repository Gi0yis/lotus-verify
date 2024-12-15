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

    public long getTotalQueries() {
        return searchResultRepository.count();
    }

    public double getPercentageWithResults() {
        List<SearchResult> results = searchResultRepository.findAll();

        var withResults = results.stream().filter(r -> r.getResult() != null && !r.getResult().isEmpty()).count();
        return ((double) withResults / results.size() * 100);
    }

    public double getAverageResponseTime() {
        return 200;
    }

    public long getQueriesByDay(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        return searchResultRepository.findAll().stream().filter(r -> r.getTimestamp().isAfter(start) &&
                r.getTimestamp().isBefore(end)).count();
    }
}
