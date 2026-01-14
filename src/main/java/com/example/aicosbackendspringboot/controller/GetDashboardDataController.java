package com.example.aicosbackendspringboot.controller;

import com.example.aicosbackendspringboot.service.GetDashboardDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class GetDashboardDataController {

    @Autowired
    private GetDashboardDataService dashboardService;

    @GetMapping("/getDashboardData")
    // [修正] 加上 @RequestParam 接收前端傳來的參數 (例如 ts=1)
    // defaultValue = "1" 是為了防止前端沒傳參數時報錯
    public ResponseEntity<?> getDashboardData(@RequestParam(value = "ts", defaultValue = "1") int ts) {
        try {
            // [修正] 將接到的 ts 參數傳入 Service
            Map<String, Object> data = dashboardService.getDashboardData(ts);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}