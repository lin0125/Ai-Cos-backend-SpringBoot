package com.example.aicosbackendspringboot.service;

import com.example.aicosbackendspringboot.repository.GetHistoryDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GetHistoryDataService {
    @Autowired
    private GetHistoryDataRepository historyRepository;

    public Map<String, Object> getHistoryData(String dataType, String startTime, String endTime,
                                              String measurement, String device, String bucket) throws Exception {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("start_time and end_time required");
        }

        // 1. 從 Repository 取得原始資料
        Map<String, Object> payload = historyRepository.fetchTimeSeries(dataType, startTime, endTime, measurement, device, bucket);

        // 2. 轉換資料點
        List<Map<String, Object>> dataPoints = toLegacyPoints(payload.get("series"));
        // 注意：這裡假設 payload 結構中有 baseline，需確保 toLegacyPoints 能正確處理 null
        Object baselineObj = ((Map<String, Object>)payload.get("series")).get("baseline");
        List<Map<String, Object>> baselinePoints = baselineObj != null ? toLegacyPoints(baselineObj) : new ArrayList<>();

        // 3. 計算統計數據 (Avg, Min, Max, Std)
        double[] values = dataPoints.stream().mapToDouble(p -> (double)p.get("value")).toArray();
        double avg = values.length > 0 ? Arrays.stream(values).average().orElse(0) : 0;
        double min = values.length > 0 ? Arrays.stream(values).min().orElse(0) : 0;
        double max = values.length > 0 ? Arrays.stream(values).max().orElse(0) : 0;
        double std = values.length > 0 ? Math.sqrt(Arrays.stream(values).map(v -> Math.pow(v - avg, 2)).average().orElse(0)) : 0;

        // 4. [新增] 計算總節能量 (Total Saved)
        // 邏輯：基線總用量 - 實際總用量
        double sumData = Arrays.stream(values).sum();
        double sumBaseline = baselinePoints.stream().mapToDouble(p -> (double) p.get("value")).sum();

        // 如果後端有傳 carbon 就用後端的，沒有的話就自己算 (基線總和 - 實際總和)
        double originalCarbon = payload.containsKey("carbon") ? ((Number) payload.get("carbon")).doubleValue() : 0.0;
        double calculatedSavings = (sumBaseline - sumData);

        // 這裡決定要用哪一個：如果後端沒傳 (0.0)，我們就用算的。如果 calculatedSavings 是負的，代表沒省到電，設為 0
        double finalSavings = originalCarbon != 0.0 ? originalCarbon : (calculatedSavings > 0 ? calculatedSavings : 0);

        // 5. 組裝回傳結果
        Map<String, Object> ret = new LinkedHashMap<>();
        ret.put("data_average", Math.round(avg * 100) / 100.0);
        ret.put("data_min", Math.round(min * 100) / 100.0);
        ret.put("data_max", Math.round(max * 100) / 100.0);
        ret.put("data_std", Math.round(std * 100) / 100.0);

        // 更新這裡：回傳計算後的節能量
        ret.put("carbon_reduction", Math.round(finalSavings * 100) / 100.0);

        ret.put("data", dataPoints);
        if (!baselinePoints.isEmpty()) {
            ret.put("baseline", baselinePoints);
        }

        return ret;
    }

    private List<Map<String, Object>> toLegacyPoints(Object series) {
        List<Map<String, Object>> out = new ArrayList<>();
        if (series instanceof List<?>) {
            for (Object pt : (List<?>) series) {
                Map<?, ?> map = (Map<?, ?>) pt;
                // 相容不同的 Key 名稱 (time/value 或 t/v)
                if (map.containsKey("time") && map.containsKey("value")) {
                    out.add(Map.of("time", map.get("time"), "value", ((Number) map.get("value")).doubleValue()));
                } else if (map.containsKey("t") && map.containsKey("v")) {
                    out.add(Map.of("time", map.get("t"), "value", ((Number) map.get("v")).doubleValue()));
                }
            }
        }
        return out;
    }
}