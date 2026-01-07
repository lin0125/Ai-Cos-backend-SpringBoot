package com.example.aicosbackendspringboot.service;

import com.example.aicosbackendspringboot.repository.SelectModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SelectModelService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${fastapi.server.url:http://localhost:8000}")
    private String fastApiBaseUrl;

    /**
     * 驗證並切換指定的 AI 模型 (串接 FastAPI 熱重載功能)
     * @param type 模型類型 (RT 或 PW)
     * @param modelId 模型在資料庫中的 ID (替代原本的 filename)
     * @return FastAPI 回傳的狀態 Map
     */
    public Map<String, Object> selectModel(String type, Integer modelId) throws IOException {
        // 1. 基本驗證 (保留原本邏輯，確保類型正確)
        String modelType = (type == null ? "RT" : type.trim().toUpperCase());
        if (!modelType.equals("RT") && !modelType.equals("PW")) {
            throw new IllegalArgumentException("Type must be RT or PW");
        }
        if (modelId == null) {
            throw new IllegalArgumentException("modelId is required");
        }

        // 2. 準備請求資訊
        String activeUrl = fastApiBaseUrl + "/api/models/active";

        // 根據 FastAPI 的 SwitchModelRequest 格式建立請求體
        Map<String, Object> request = new HashMap<>();
        if ("RT".equals(modelType)) {
            request.put("rt_model_id", modelId);
            request.put("pw_model_id", null);
        } else {
            request.put("rt_model_id", null);
            request.put("pw_model_id", modelId);
        }

        try {
            // 3. 發送 POST 請求觸發 FastAPI 更新 DB 並執行 reload_state
            ResponseEntity<Map> response = restTemplate.postForEntity(activeUrl, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // 回傳包含新狀態的資料
                return response.getBody();
            } else {
                throw new RuntimeException("FastAPI 切換模型失敗");
            }
        } catch (Exception e) {
            throw new IOException("連線至 FastAPI 執行熱重載失敗: " + e.getMessage());
        }
    }
}

//        String modelType = (type == null ? "RT" : type.trim().toUpperCase());
//        if (!modelType.equals("RT") && !modelType.equals("PW")) {
//            throw new IllegalArgumentException("type RT|PW and filename required");
//        }
//        if (filename == null || filename.trim().isEmpty()) {
//            throw new IllegalArgumentException("type RT|PW and filename required");
//        }
//
//        List<String> files = modelRepository.listModelFiles(modelType);
//        if (!files.contains(filename)) {
//            throw new FileNotFoundException("filename not found");
//        }
//
//        // 寫入 ACTIVE.txt
//        modelRepository.writeActiveFile(modelType, filename);
//
//        // 重新載入模型（此功能須根據原 mm.reload_models 對應 Java 實作）
//        boolean changed = modelRepository.reloadModels();
//
//        return Map.of("status", "ok", "type", modelType, "active", filename, "reloaded", changed);
//    }
//
//}
