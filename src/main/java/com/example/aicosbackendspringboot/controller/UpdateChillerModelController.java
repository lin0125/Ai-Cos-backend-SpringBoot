package com.example.aicosbackendspringboot.controller;

import com.example.aicosbackendspringboot.service.UpdateChillerModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class UpdateChillerModelController {



    @Autowired
    private UpdateChillerModelService Chillerservice;

    /**
     * 接收模型檔案上傳請求
     * @param file 上傳的檔案 (multipart/form-data)
     * @param type 模型類型 "RT" 或 "PW"
     * @return 返回處理結果的 JSON
     */
    @PostMapping("/updateChillerModel")
    public ResponseEntity<?> updateChillerModel(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "RT") String type) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "file is required"));
        }

        try {
            Map<String, String> result = Chillerservice.saveModel(file, type);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // 紀錄日誌 Log the exception e
            return ResponseEntity.status(500).body(Map.of("error", "模型上傳失敗: " + e.getMessage()));
        }
    }

}
