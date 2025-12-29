package com.example.aicosbackendspringboot.tool;

public class KwhResult {
    private final double kw;
    private final double kwh;

    public KwhResult(double kw, double kwh) {
        this.kw = kw;
        this.kwh = kwh;
    }
    public double getKw() { return kw; }
    public double getKwh() { return kwh; }
}
