package com.example.aicosbackendspringboot.controller;

import com.example.aicosbackendspringboot.service.GetChillersDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/getChillersData")
public class GetChillersDataController {

    @Autowired
    private GetChillersDataService chillerService;

    /**
     * 取得冰機狀態與溫度資料
     * @return JSON 格式回傳冰機溫度及線上冰機 ID
     */
    @GetMapping
    public ResponseEntity<?> getChillersData() {
        try {
            Map<String, Object> data = chillerService.getChillersData();
            if (data.isEmpty()) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(Map.of("error", "冰機資料尚未就緒"));
            }
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "API execution failed", "details", e.getMessage()));
        }
    }
}
