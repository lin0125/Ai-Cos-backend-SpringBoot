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

        Map<String, Object> payload = historyRepository.fetchTimeSeries(dataType, startTime, endTime, measurement, device, bucket);

        List<Map<String, Object>> dataPoints = toLegacyPoints(payload.get("series"));
        List<Map<String, Object>> baselinePoints = toLegacyPoints(((Map<String, Object>)payload.get("series")).get("baseline"));

        double[] values = dataPoints.stream().mapToDouble(p -> (double)p.get("value")).toArray();
        double avg = values.length > 0 ? Arrays.stream(values).average().orElse(0) : 0;
        double min = values.length > 0 ? Arrays.stream(values).min().orElse(0) : 0;
        double max = values.length > 0 ? Arrays.stream(values).max().orElse(0) : 0;
        double std = values.length > 0 ? Math.sqrt(Arrays.stream(values).map(v -> Math.pow(v - avg, 2)).average().orElse(0)) : 0;

        Map<String, Object> ret = new LinkedHashMap<>();
        ret.put("data_average", Math.round(avg * 100) / 100.0);
        ret.put("data_min", Math.round(min * 100) / 100.0);
        ret.put("data_max", Math.round(max * 100) / 100.0);
        ret.put("data_std", Math.round(std * 100) / 100.0);
        ret.put("carbon_reduction", payload.getOrDefault("carbon", 0.0));
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
