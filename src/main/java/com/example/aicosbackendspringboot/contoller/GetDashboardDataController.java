package com.example.aicosbackendspringboot.contoller;

import com.example.aicosbackendspringboot.service.GetDashboardDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.ServiceUnavailableException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GetDashboardDataController {

    @Autowired
    private GetDashboardDataService dashboardService;

    @GetMapping("/getDashboardData")
    public ResponseEntity<?> getDashboardData(@RequestParam(required = false) Integer count) {
        try {
            if (count == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "count 參數必須提供"));
            }
            return ResponseEntity.ok(dashboardService.getDashboardData(count));
        } catch (ServiceUnavailableException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Dashboard 尚未就緒"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "API 執行失敗", "detail", e.getMessage()));
        }
    }
}
