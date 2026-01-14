package com.example.aicosbackendspringboot.service;

import com.example.aicosbackendspringboot.dtos.chiller.ChillerData;
import com.example.aicosbackendspringboot.repository.GetChillersDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GetChillersDataService {

    @Autowired
    private GetChillersDataRepository repository;

    public Map<String, Object> getChillersData() throws Exception {
        ChillerData rawData = repository.readHourlyChillerData();
        Map<String, Object> response = new HashMap<>();

        if (rawData == null) {
            return Map.of("Online_Chiller_ID", new ArrayList<>(), "Chiller_1_Temp", 0.0, "Chiller_2_Temp", 0.0);
        }

        Map<String, Double> signals = rawData.getSignalMean();
        Map<String, Double> temps = rawData.getTempMean();

        // --- 1. 先宣告所有需要的變數 ---
        List<String> onlineChillers = new ArrayList<>();
        Double sp1 = 0.0;
        Double sp2 = 0.0;

        // --- 2. 獲取訊號值 (只定義一次) ---
        Double sig1 = signals.getOrDefault("Chiller_1_Signal", 0.0);
        Double sig2 = signals.getOrDefault("Chiller_2_Signal", 0.0);

        System.out.println("📡 [Service] 檢查訊號 - Chiller1: " + sig1 + ", Chiller2: " + sig2);

        // --- 3. 執行判斷邏輯 ---
        if (sig1 != null && sig1 > 0.5) {
            onlineChillers.add("1");
            sp1 = temps.getOrDefault("Chiller_1_T_SP", 0.0);
        }

        if (sig2 != null && sig2 > 0.5) {
            onlineChillers.add("2");
            sp2 = temps.getOrDefault("Chiller_2_T_SP", 0.0);
        }

        // --- 4. 組裝回傳結果 ---
        response.put("Online_Chiller_ID", onlineChillers);
        // 四捨五入到小數點第二位
        response.put("Chiller_1_Temp", Math.round(sp1 * 100.0) / 100.0);
        response.put("Chiller_2_Temp", Math.round(sp2 * 100.0) / 100.0);

        // 為了相容前端可能需要的陣列格式
        response.put("chillers_temperature", Arrays.asList(sp1, sp2));

        System.out.println("📦 [Service] 最終回傳 JSON: " + response);
        return response;
    }
}