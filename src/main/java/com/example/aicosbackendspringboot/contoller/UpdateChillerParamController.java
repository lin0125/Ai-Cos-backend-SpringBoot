package com.example.aicosbackendspringboot.contoller;

import com.example.aicosbackendspringboot.dtos.chiller.ChillerConfig;
import com.example.aicosbackendspringboot.service.UpdateChillerParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UpdateChillerParamController {
    @Autowired
    private UpdateChillerParamService chillerService;

    @PostMapping("/updateChillerParam")
    public ResponseEntity<?> updateChillerParam(@RequestBody ChillerConfig dto) {
        try {
            chillerService.updateChillerConfig(dto);
            return ResponseEntity.ok().body("{\"Contents\":\"success\"}");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\":\"參數更新失敗\"}");
        }
    }
}
