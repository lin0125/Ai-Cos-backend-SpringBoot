package com.example.aicosbackendspringboot.tool;

public class TempControlRule {
    public static double apply(double temp) {
        if (temp >= 1) {
            return 1.0;
        } else if (temp <= -1) {
            return -1.0;
        }
        return temp;
    }
}
