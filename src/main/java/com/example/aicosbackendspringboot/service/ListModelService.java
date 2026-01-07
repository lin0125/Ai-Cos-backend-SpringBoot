package com.example.aicosbackendspringboot.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ListModelService {

    @Value("${fastapi.server.url:http://localhost:8000}")
    private String fastApiBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> listModels(String type) {
        String modelType = type.toLowerCase(); // FastAPI 預期小寫 key

        if (!modelType.equals("rt") && !modelType.equals("pw")) {
            throw new IllegalArgumentException("type must be RT or PW");
        }

        // 定義 API 端點
        String statusUrl = fastApiBaseUrl + "/api/models/status";
        String inventoryUrl = fastApiBaseUrl + "/api/models/inventory";

        try {
            // 1. 取得當前狀態 (Status)
            ResponseEntity<Map> statusResponse = restTemplate.getForEntity(statusUrl, Map.class);
            Map<String, Object> statusBody = statusResponse.getBody();

            // 2. 取得所有模型清單 (Inventory)
            ResponseEntity<Map> inventoryResponse = restTemplate.getForEntity(inventoryUrl, Map.class);
            Map<String, Object> inventoryBody = inventoryResponse.getBody();

            // 準備最終合併的結果
            Map<String, Object> result = new HashMap<>();

            // 解析當前 active 的模型
            if (statusBody != null && statusBody.get("active_slots") != null) {
                Map<String, Object> activeSlots = (Map<String, Object>) statusBody.get("active_slots");
                result.put("active", activeSlots.get(modelType)); // 取得對應類型的當前模型資訊
            }

            // 解析該類型的可選清單 (rt_candidates 或 pw_candidates)
            if (inventoryBody != null) {
                String candidateKey = modelType + "_candidates"; // 組成 rt_candidates 或 pw_candidates
                result.put("files", inventoryBody.get(candidateKey));
            }

            // 補充基本資訊
            result.put("type", type.toUpperCase());
            result.put("service_status", statusBody != null ? statusBody.get("service_status") : "unknown");

            return result;

        } catch (Exception e) {
            throw new RuntimeException("連線至 FastAPI 失敗: " + e.getMessage());
        }
    }
}
