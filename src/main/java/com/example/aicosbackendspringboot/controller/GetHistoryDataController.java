package com.example.aicosbackendspringboot.controller;

import com.example.aicosbackendspringboot.service.GetHistoryDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class GetHistoryDataController {
    @Autowired
    private GetHistoryDataService historyService;

    @GetMapping("/getHistoryData")
    public ResponseEntity<?> getHistoryData(
            @RequestParam String data_type,
            @RequestParam String start_time,
            @RequestParam String end_time,
            @RequestParam(required=false) String measurement,
            @RequestParam(required=false) String device,
            @RequestParam(required=false) String bucket
    ) {
        try {
            return ResponseEntity.ok(
                    historyService.getHistoryData(data_type, start_time, end_time, measurement, device, bucket)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
