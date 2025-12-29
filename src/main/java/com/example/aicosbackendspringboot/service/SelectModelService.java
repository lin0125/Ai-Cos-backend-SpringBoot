package com.example.aicosbackendspringboot.service;

import com.example.aicosbackendspringboot.repository.SelectModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class SelectModelService {

    @Autowired
    private SelectModelRepository modelRepository;

    /**
     * 驗證並設定指定模型為啟用
     * @param type 模型類型 (RT 或 PW)
     * @param filename 模型檔名
     * @return 狀態 Map
     */
    public Map<String, Object> selectModel(String type, String filename) throws IOException {
        String modelType = (type == null ? "RT" : type.trim().toUpperCase());
        if (!modelType.equals("RT") && !modelType.equals("PW")) {
            throw new IllegalArgumentException("type RT|PW and filename required");
        }
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("type RT|PW and filename required");
        }

        List<String> files = modelRepository.listModelFiles(modelType);
        if (!files.contains(filename)) {
            throw new FileNotFoundException("filename not found");
        }

        // 寫入 ACTIVE.txt
        modelRepository.writeActiveFile(modelType, filename);

        // 重新載入模型（此功能須根據原 mm.reload_models 對應 Java 實作）
        boolean changed = modelRepository.reloadModels();

        return Map.of("status", "ok", "type", modelType, "active", filename, "reloaded", changed);
    }

}
