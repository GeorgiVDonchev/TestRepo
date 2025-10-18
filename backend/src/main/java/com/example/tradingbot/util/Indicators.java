package com.example.tradingbot.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public final class Indicators {
    private Indicators() {}

    public static List<Double> simpleMovingAverage(List<Double> values, int window) {
        List<Double> out = new ArrayList<>(values.size());
        if (window <= 0) throw new IllegalArgumentException("window must be > 0");
        double sum = 0.0;
        Deque<Double> q = new ArrayDeque<>();
        for (double v : values) {
            sum += v;
            q.addLast(v);
            if (q.size() > window) {
                sum -= q.removeFirst();
            }
            if (q.size() == window) {
                out.add(sum / window);
            } else {
                out.add(Double.NaN);
            }
        }
        return out;
    }
}
