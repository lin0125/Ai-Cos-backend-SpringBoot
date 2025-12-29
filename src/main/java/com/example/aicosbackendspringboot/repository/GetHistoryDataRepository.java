package com.example.aicosbackendspringboot.repository;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Repository
public class GetHistoryDataRepository {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String UP_CORE_BASE = "http://localhost:5001";

    public Map<String, Object> fetchTimeSeries(String dataType, String startTime, String endTime,
                                               String measurement, String device, String bucket) throws Exception {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(UP_CORE_BASE + "/timeseries")
                .queryParam("measurement", measurement != null ? measurement : "water_metrics")
                .queryParam("data_type", dataType)
                .queryParam("start_time", startTime)
                .queryParam("end_time", endTime);

        if (device != null) uriBuilder.queryParam("device", device);
        if (bucket != null) uriBuilder.queryParam("bucket", bucket);

        ResponseEntity<Map> response = restTemplate.getForEntity(uriBuilder.toUriString(), Map.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Failed to fetch time series data");
        }

        return response.getBody();
    }
}
