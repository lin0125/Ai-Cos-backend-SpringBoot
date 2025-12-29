package com.example.aicosbackendspringboot.dtos.chiller;

import lombok.Data;

import java.util.Map;

@Data
public class ChillerData {

    private Map<String, Double> signalMean;
    private Map<String, Double> tempMean;

    public ChillerData(Map<String, Double> signalMean, Map<String, Double> tempMean) {
        this.signalMean = signalMean;
        this.tempMean = tempMean;
    }

    public  Map<String, Double> getSignalMean() {
        return signalMean;
    }

    public Map<String, Double> getTempMean() {
        return tempMean;
    }
}
