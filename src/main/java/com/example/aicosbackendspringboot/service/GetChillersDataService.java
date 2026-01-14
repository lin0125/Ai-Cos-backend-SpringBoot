package com.example.aicosbackendspringboot.service;

import com.example.aicosbackendspringboot.dtos.chiller.ChillerData;
import com.example.aicosbackendspringboot.repository.GetChillersDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GetChillersDataService {

    @Autowired
    private GetChillersDataRepository repository;

    public Map<String, Object> getChillersData() throws Exception {
        // 1. 從 Repository 取得資料
        ChillerData rawData = repository.readHourlyChillerData();
        Map<String, Object> response = new HashMap<>();

        // 2. 判斷冰機是否啟動
        List<String> onlineChillers = new ArrayList<>();
        Double signal1 = rawData.getSignalMean().getOrDefault("Chiller_1_Signal", 0.0);
        Double signal2 = rawData.getSignalMean().getOrDefault("Chiller_2_Signal", 0.0);

        if (signal1 > 0.5) onlineChillers.add("1");
        if (signal2 > 0.5) onlineChillers.add("2");

        // 3. 取得溫度 (關鍵修正點)
        // Repository 已經確保 Evap_Out_Temp 一定有值 (因為做了補值)
        // 但為了保險，我們可以多試幾個可能的 Key
        Double temp1 = getTemp(rawData.getTempMean(), "Chiller_1_Evap_Out_Temp", "Chiller_1_T_SP");
        Double temp2 = getTemp(rawData.getTempMean(), "Chiller_2_Evap_Out_Temp", "Chiller_2_T_SP");

        System.out.println("🌡️ [Service] 最終讀取到的溫度 - Chiller 1: " + temp1);
        System.out.println("🌡️ [Service] 最終讀取到的溫度 - Chiller 2: " + temp2);

        // 4. 組裝回傳
        response.put("Online_Chiller_ID", onlineChillers);
        response.put("Chiller_1_Temp", Math.round(temp1 * 100.0) / 100.0);
        response.put("Chiller_2_Temp", Math.round(temp2 * 100.0) / 100.0);

        return response;
    }

    // 輔助方法：嘗試讀取多個 Key，回傳第一個找到的值
    private Double getTemp(Map<String, Double> map, String... keys) {
        if (map == null) return 0.0;
        for (String key : keys) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
        }
        return 0.0;
    }
}