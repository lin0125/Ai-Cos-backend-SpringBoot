package com.example.aicosbackendspringboot.contoller;

import com.example.aicosbackendspringboot.dtos.request.SelectModelRequest;
import com.example.aicosbackendspringboot.service.SelectModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.Map;

@RestController
@RequestMapping("/selectModel")
public class SelectModelController {

    @Autowired
    private SelectModelService modelService;

    /**
     * 指定啟用模型，更新 ACTIVE.txt 檔案
     * @param request 包含 type (RT|PW) 與 filename (模型檔名)
     * @return 狀態與更新結果
     */
    @PostMapping
    public ResponseEntity<?> selectModel(@RequestBody SelectModelRequest request) {
        try {
            // 將原本的 request.getFilename() 改為 request.getModelId()
            // 這樣傳入 Service 的參數類型就會是 Integer，符合您 Service 的定義
            Map<String, Object> result = modelService.selectModel(request.getType(), request.getModelId());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
