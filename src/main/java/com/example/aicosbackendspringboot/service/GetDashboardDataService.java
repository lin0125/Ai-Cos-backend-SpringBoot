package com.example.aicosbackendspringboot.service;

import com.example.aicosbackendspringboot.repository.GetDashboardDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.naming.ServiceUnavailableException;
import java.util.Map;

@Service
public class GetDashboardDataService {
    @Autowired
    private GetDashboardDataRepository dashboardRepository;

    private Map<String, Object> latestDashboard;

    public Map<String, Object> getDashboardData(int count) throws Exception {
        if (latestDashboard == null) {
            throw new ServiceUnavailableException();
        }
        Map<String, Object> ret = dashboardRepository.buildDashboardData(count);
        latestDashboard = ret;
        return ret;
    }
    // 每 5 秒執行一次 (單位毫秒)
    @Scheduled(fixedRate = 5000)
    public void refreshDashboardData() {
        try {
            // 這裡的 count 邏輯需要釐清，Python 是從 request 傳入，
            // 但如果是背景排程，你需要決定 count (小時) 是當前時間還是固定值？
            int currentHour = java.time.LocalDateTime.now().getHour();
            this.latestDashboard = dashboardRepository.buildDashboardData(currentHour);
        } catch (Exception e) {
            e.printStackTrace(); // 記錄錯誤但不要讓服務崩潰
        }
    }
}
