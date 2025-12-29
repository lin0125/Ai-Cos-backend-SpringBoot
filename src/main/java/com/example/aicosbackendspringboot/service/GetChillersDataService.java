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
    private GetChillersDataRepository chillerRepository;

    @Autowired
    private WriteApiLogService apiLogService; // 用來記錄 API 執行狀態

    /**
     * 取得冰機狀態及溫度資料
     */
    public Map<String, Object> getChillersData() {
        Map<String, Object> retDict = new HashMap<>();
        List<Integer> onlineChillerId = new ArrayList<>();
        List<Double> chillersTemperature = new ArrayList<>();

        try {
            // 從 Repository 層讀取指F定小時的 CSV 冰機資料
            ChillerData chillerData = chillerRepository.readHourlyChillerData();
            Map<String, Double> signalState = chillerData.getSignalMean();
            Map<String, Double> tempState = chillerData.getTempMean();

            // 判斷冰機是否在線 (根據 Signal 值 > 0.5)
            if (signalState.get("Chiller_1_Signal") > 0.5) {
                onlineChillerId.add(1);
                chillersTemperature.add(tempState.get("Chiller_1_T_SP"));
            }
            if (signalState.get("Chiller_2_Signal") > 0.5) {
                onlineChillerId.add(2);
                chillersTemperature.add(tempState.get("Chiller_2_T_SP"));
            }

            retDict.put("chillers_temperature", chillersTemperature);
            retDict.put("online_chiller_id", onlineChillerId);

            apiLogService.writeApiLog("getChillersData", "success", retDict,null);
            return retDict;
        } catch (Exception e) {
            apiLogService.writeApiLog("getChillersData", "fail",null, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
