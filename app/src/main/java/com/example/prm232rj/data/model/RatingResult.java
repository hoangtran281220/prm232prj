package com.example.prm232rj.data.model;

public class RatingResult {
    private double average;
    private long count;

    public RatingResult(double average, long count) {
        this.average = average;
        this.count = count;
    }

    public double getAverage() {
        return average;
    }

    public long getCount() {
        return count;
    }
}
