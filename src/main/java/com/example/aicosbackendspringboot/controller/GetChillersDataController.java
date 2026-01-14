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
// [修正 1] 將 Controller 的基礎路徑改為 /api，這樣所有裡面的方法都會以此開頭
@RequestMapping("/api")
public class GetChillersDataController {

    @Autowired
    private GetChillersDataService chillerService;

    /**
     * 取得冰機狀態與溫度資料
     * 完整路徑變成: /api/getChillersData
     */
    // [修正 2] 明確指定方法路徑
    @GetMapping("/getChillersData")
    public ResponseEntity<?> getChillersData() {
        try {
            Map<String, Object> data = chillerService.getChillersData();

            // 這裡可以把 isEmpty 的檢查拿掉或保留，看你的需求
            // 如果回傳空資料 {} 給前端，前端只要判斷欄位是否存在即可，不一定要報錯
            if (data == null || data.isEmpty()) {
                // 建議：即使沒資料也回傳 200 OK 和空物件，讓前端顯示 "--" 即可，不要回傳錯誤代碼
                return ResponseEntity.ok(Map.of());
            }
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "API execution failed", "details", e.getMessage()));
        }
    }
}