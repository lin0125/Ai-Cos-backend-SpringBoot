package com.example.aicosbackendspringboot.tool;

import java.util.List;
import java.util.Map;

public class ControlPredict {
    public static double run(List<Map<String, String>> hourData, List<String> labels) {

        double sum = 0.0;
        int count = 0;
        for (Map<String, String> row : hourData) {
            for (String label : labels) {
                try {
                    double value = Double.parseDouble(row.get(label));
                    sum += value;
                    count++;
                } catch (Exception e) {
                }
            }
        }
        return (count > 0) ? sum / count : 0.0;
    }
}
