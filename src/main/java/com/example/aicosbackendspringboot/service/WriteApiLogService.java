package com.example.aicosbackendspringboot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class WriteApiLogService {
    /**
     * 寫入 API 呼叫紀錄檔
     * @param endpoint API 名稱 (不含 / 前綴)
     * @param status API 執行結果狀態 ("success" / "fail" 等)
     * @param data 可選回傳資料物件
     * @param error 可選錯誤訊息或例外物件
     */
    public void writeApiLog(String endpoint, String status, Object data, Object error) {
        try {
            // 確保目錄存在
            Path logDir = Paths.get("logs/api");
            Files.createDirectories(logDir);

            // 建立當日的 log 檔案路徑
            String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            Path logPath = logDir.resolve(endpoint + "_" + dateStr + ".log");

            // 使用 FileWriter 以附加模式寫入
            try (BufferedWriter writer = Files.newBufferedWriter(logPath, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                writer.write("==== API CALL ====\n");
                writer.write("Time: " + LocalDateTime.now().toString() + "\n");
                writer.write("Endpoint: /" + endpoint + "\n");
                writer.write("Status: " + status + "\n");

                // 寫入資料內容（若存在）
                if (data != null) {
                    String jsonData = new ObjectMapper()
                            .writerWithDefaultPrettyPrinter()
                            .writeValueAsString(data);
                    writer.write("Data: " + jsonData + "\n");
                }

                // 寫入錯誤內容（若存在）
                if (error != null) {
                    writer.write("Error: " + error.toString() + "\n");
                    if (error instanceof Throwable) {
                        StringWriter sw = new StringWriter();
                        ((Throwable) error).printStackTrace(new PrintWriter(sw));
                        writer.write(sw.toString());
                    }
                }
                writer.write("\n");
            }
        } catch (IOException logErr) {
            System.err.println("[!] Failed to write API log: " + logErr.getMessage());
        }
    }
}
