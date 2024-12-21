package com.lotusverify.lotusapp.model;

public class Metrics {
    private long totalQueries;
    private double searchSuccessRate;
    private double precisionRate;
    private double averageResponseTime;
    private double averageRelevancyScore;
    private long truePositives;
    private long falsePositives;
    private long trueNegatives;
    private long falseNegatives;
    private double falsePositiveRate;
    private double falseNegativeRate;
    private double accuracyRate;
    private double f1Score;

    public Metrics(long totalQueries, double searchSuccessRate, double precisionRate, double averageResponseTime,
                   double averageRelevancyScore, long truePositives, long falsePositives, long trueNegatives,
                   long falseNegatives, double falsePositiveRate, double falseNegativeRate,
                   double accuracyRate, double f1Score) {
        this.totalQueries = totalQueries;
        this.searchSuccessRate = searchSuccessRate;
        this.precisionRate = precisionRate;
        this.averageResponseTime = averageResponseTime;
        this.averageRelevancyScore = averageRelevancyScore;
        this.truePositives = truePositives;
        this.falsePositives = falsePositives;
        this.trueNegatives = trueNegatives;
        this.falseNegatives = falseNegatives;
        this.falsePositiveRate = falsePositiveRate;
        this.falseNegativeRate = falseNegativeRate;
        this.accuracyRate = accuracyRate;
        this.f1Score = f1Score;
    }

    public long getTotalQueries() {
        return totalQueries;
    }

    public double getSearchSuccessRate() {
        return searchSuccessRate;
    }

    public double getPrecisionRate() {
        return precisionRate;
    }

    public double getAverageResponseTime() {
        return averageResponseTime;
    }

    public double getAverageRelevancyScore() {
        return averageRelevancyScore;
    }

    public long getTruePositives() {
        return truePositives;
    }

    public long getFalsePositives() {
        return falsePositives;
    }

    public long getTrueNegatives() {
        return trueNegatives;
    }

    public long getFalseNegatives() {
        return falseNegatives;
    }

    public double getFalsePositiveRate() {
        return falsePositiveRate;
    }

    public double getFalseNegativeRate() {
        return falseNegativeRate;
    }

    public double getAccuracyRate() {
        return accuracyRate;
    }

    public double getF1Score() {
        return f1Score;
    }
}
