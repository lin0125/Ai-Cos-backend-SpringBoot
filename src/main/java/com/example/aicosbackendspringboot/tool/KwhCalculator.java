package com.example.aicosbackendspringboot.tool;

import java.util.List;
import java.util.Map;

public class KwhCalculator {
    public static KwhResult calculate(List<Map<String, String>> hourData, List<String> kwLabel, List<String> totLabel, int hour) {
        double totalKw = 0.0;
        for (Map<String, String> row : hourData) {
            for (String label : totLabel) {
                try {
                    totalKw += Double.parseDouble(row.get(label));
                } catch (Exception e) {
                }
            }
        }
        return new KwhResult(totalKw, totalKw * hour); // 假設 kWh = KW * 時間
    }
}
