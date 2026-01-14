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

        // 如果沒資料，回傳基本結構避免前端報錯
        if (rawData == null || rawData.getSignalMean() == null) {
            response.put("Online_Chiller_ID", new ArrayList<>());
            response.put("Chiller_1_Temp", 0.0);
            response.put("Chiller_2_Temp", 0.0);
            response.put("chillers_temperature", Arrays.asList(0.0, 0.0));
            return response;
        }

        Map<String, Double> signals = rawData.getSignalMean();
        Map<String, Double> temps = rawData.getTempMean();

        List<String> onlineChillers = new ArrayList<>();
        Double sp1 = temps.getOrDefault("Chiller_1_T_SP", 0.0);
        Double sp2 = temps.getOrDefault("Chiller_2_T_SP", 0.0);

        // 判斷訊號 (這部分邏輯現在依賴 Repository 的參數順序修正)
        Double sig1 = signals.getOrDefault("Chiller_1_Signal", 0.0);
        Double sig2 = signals.getOrDefault("Chiller_2_Signal", 0.0);

        if (sig1 > 0.5) onlineChillers.add("1");
        if (sig2 > 0.5) onlineChillers.add("2");

        response.put("Online_Chiller_ID", onlineChillers);
        // 無論是否有開機，都回傳 SP 值，讓前端決定怎麼顯示
        response.put("Chiller_1_Temp", Math.round(sp1 * 100.0) / 100.0);
        response.put("Chiller_2_Temp", Math.round(sp2 * 100.0) / 100.0);
        response.put("chillers_temperature", Arrays.asList(sp1, sp2));

        return response;
    }
}