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
        System.out.println("🚀 [Service] 被呼叫：getChillersData()");

        // 1. 從 Repository 取得原始資料
        ChillerData rawData = repository.readHourlyChillerData();

        if (rawData == null || rawData.getSignalMean() == null) {
            System.out.println("⚠️ [Service] 取得的 Raw Data 為空！");
            return Map.of("Online_Chiller_ID", new ArrayList<>(), "Chiller_1_Temp", 0.0, "Chiller_2_Temp", 0.0);
        }

        Map<String, Object> response = new HashMap<>();

        // 2. 判斷冰機是否啟動
        List<String> onlineChillers = new ArrayList<>();

        // 取得訊號值 (預設為 0.0)
        Double signal1 = rawData.getSignalMean().getOrDefault("Chiller_1_Signal", 0.0);
        Double signal2 = rawData.getSignalMean().getOrDefault("Chiller_2_Signal", 0.0);

        // 印出訊號值
        System.out.println("📡 [Service] 訊號值檢查:");
        System.out.println("   - Chiller 1 Signal: " + signal1 + " (閾值 0.5)");
        System.out.println("   - Chiller 2 Signal: " + signal2 + " (閾值 0.5)");

        if (signal1 > 0.5) {
            onlineChillers.add("1");
            System.out.println("   => 判定: 冰機 1 [開啟]");
        } else {
            System.out.println("   => 判定: 冰機 1 [關閉]");
        }

        if (signal2 > 0.5) {
            onlineChillers.add("2");
            System.out.println("   => 判定: 冰機 2 [開啟]");
        } else {
            System.out.println("   => 判定: 冰機 2 [關閉]");
        }

        // 3. 取得溫度
        Double temp1 = rawData.getTempMean().getOrDefault("Chiller_1_T_SP", 0.0); // 注意：確認你的 CSV 欄位名稱是這個嗎？還是 Chiller_1_Evap_Out_Temp ?
        Double temp2 = rawData.getTempMean().getOrDefault("Chiller_2_T_SP", 0.0);

        System.out.println("🌡️ [Service] 溫度值檢查:");
        System.out.println("   - Chiller 1 Temp: " + temp1);
        System.out.println("   - Chiller 2 Temp: " + temp2);

        // 4. 組裝回傳
        response.put("Online_Chiller_ID", onlineChillers);
        response.put("Chiller_1_Temp", Math.round(temp1 * 100.0) / 100.0);
        response.put("Chiller_2_Temp", Math.round(temp2 * 100.0) / 100.0);

        // [重要] 控制溫度 (Dashboard Data) 的邏輯通常在另一個 API (GetDashboardData)，但如果你想在這裡也檢查，可以加 log

        System.out.println("📦 [Service] 最終回傳 JSON: " + response);
        System.out.println("========================================");

        return response;
    }
}