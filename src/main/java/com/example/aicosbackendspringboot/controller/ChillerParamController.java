package com.example.aicosbackendspringboot.controller;

import com.example.aicosbackendspringboot.dtos.chiller.ChillerConfig;
import com.example.aicosbackendspringboot.repository.GetChillerParamRepository;
import com.example.aicosbackendspringboot.repository.UpdateChillerParamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chiller")
@CrossOrigin(origins = "http://localhost:4200") // 允許前端存取
public class ChillerParamController {

    @Autowired
    private GetChillerParamRepository getRepo;

    @Autowired
    private UpdateChillerParamRepository updateRepo;

    // 1. 讓前端讀取當前的設定值 (JSON)
    @GetMapping("/params")
    public ResponseEntity<?> getParams() {
        return getRepo.findById("default")
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 2. 讓前端更新設定值 (寫入 JSON)
    @PostMapping("/update-params")
    public ResponseEntity<?> updateParams(@RequestBody ChillerConfig config) {
        try {
            updateRepo.saveConfig(config);
            return ResponseEntity.ok().body("{\"message\": \"設定更新成功\"}");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"更新失敗: " + e.getMessage() + "\"}");
        }
    }
}
