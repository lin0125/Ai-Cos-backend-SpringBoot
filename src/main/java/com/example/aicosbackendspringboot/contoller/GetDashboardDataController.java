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
    // ★★★ 修改這裡：加上 value = "ts" ★★★
    public ResponseEntity<?> getDashboardData(@RequestParam(value = "ts", required = false) Integer count) {
        try {
            // 防呆：如果前端連 ts 都沒傳，就預設用當前小時
            if (count == null) {
                count = java.time.LocalDateTime.now().getHour();
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
