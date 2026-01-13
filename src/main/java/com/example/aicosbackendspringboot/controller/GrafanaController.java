package com.example.aicosbackendspringboot.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api") // ★★★ 這裡一定要有 /api，不然前端找不到
public class GrafanaController {

    @Value("${grafana.host:http://localhost:3000}")
    private String grafanaHost;

    // 這是你剛剛提供的正確 UID
    private String dashboardUid = "a84a4847-074d-44c1-a443-30f57410b129";

    @GetMapping("/grafana-embed")
    public ResponseEntity<?> getGrafanaEmbedUrl(
            @RequestParam String panelId,
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(defaultValue = "light") String theme
    ) {
        // 為了 debug，你可以加上這一行印出訊息到後端 Log
        System.out.println("Grafana API Called: panelId=" + panelId);

        String url = String.format("%s/d-solo/%s/dashboard?orgId=1&panelId=%s&from=%s&to=%s&theme=%s",
                grafanaHost, dashboardUid, panelId, from, to, theme);

        return ResponseEntity.ok(Map.of("url", url));
    }
}