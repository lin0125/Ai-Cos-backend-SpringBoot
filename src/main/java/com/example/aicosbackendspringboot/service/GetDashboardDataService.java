package com.example.aicosbackendspringboot.service;

import com.example.aicosbackendspringboot.repository.GetDashboardDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
}
