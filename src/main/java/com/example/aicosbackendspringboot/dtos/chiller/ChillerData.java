package com.example.aicosbackendspringboot.dtos.chiller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor // 這是原本就有的，產生全參數建構子
@NoArgsConstructor  // [新增] 這會自動產生無參數建構子
public class ChillerData {

    private Map<String, Double> signalMean;
    private Map<String, Double> tempMean;

//    public ChillerData(Map<String, Double> signalMean, Map<String, Double> tempMean) {
//        this.signalMean = signalMean;
//        this.tempMean = tempMean;
//    }

    public  Map<String, Double> getSignalMean() {
        return signalMean;
    }

    public Map<String, Double> getTempMean() {
        return tempMean;
    }
}
