package com.example.aicosbackendspringboot.repository;

import com.example.aicosbackendspringboot.tool.*;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.FileNotFoundException; // 記得 import 這個
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class GetDashboardDataRepository {
    void updateTempConf(double ctemp) throws Exception {
        // ... (updateTempConf 保持不變，省略以節省版面) ...
        try (BufferedReader fr = Files.newBufferedReader(Paths.get("temp_conf.txt"))) {
            String firstLine = fr.readLine();
            String secondLine = fr.readLine();
            double d1 = Double.parseDouble(secondLine.split(",")[0]) + ctemp;
            double d2 = Double.parseDouble(secondLine.split(",")[1]) + ctemp;
            try (var fw = Files.newBufferedWriter(Paths.get("temp_conf.txt"))) {
                fw.write(firstLine + "\n");
                fw.write(d1 + "," + d2);
            }
            try (var fw = Files.newBufferedWriter(Paths.get("output.csv"))) {
                fw.write(firstLine + "\n");
                fw.write(d1 + "," + d2);
            }
        }
    }

    public Map<String, Object> buildDashboardData(int count) throws Exception {
        // 1. 日期與檔案路徑處理
        LocalDateTime now = LocalDateTime.now();
        String year = String.valueOf(now.getYear());
        String month = String.format("%02d", now.getMonthValue());
        String day = String.format("%02d", now.getDayOfMonth());
        String hour = String.format("%02d", count);

        // 2. 判斷 output_state
        Path outputStatePath = Paths.get("output_state.txt");
        String outputPath = "output.csv";

        if (Files.exists(outputStatePath)) {
            try (BufferedReader br = Files.newBufferedReader(outputStatePath)) {
                String line = br.readLine();
                if (!"0".equals(line)) {
                    outputPath = "F:/Desktop/Ai-Cos-backend-SpringBoot/datasets" + year + month + day + "-" + hour + ".csv";
                }
            }
        } else {
            System.out.println("⚠️ output_state.txt 不存在，使用預設路徑: " + outputPath);
        }

        // 定義欄位名稱
        final List<String> labels = Arrays.asList(
                "Chiller_1_F_CHW_in", "Chiller_1_T_CHW_out", "Chiller_1_T_CHW_in",
                "Weather_T", "Weather_H", "Chiller_1_KW_tot",
                "Chiller_1_Signal", "Chiller_2_Signal", "Chiller_1_T_CW_in", "Chiller_1_Load"
        );
        final List<String> kwLabel = Arrays.asList(
                "Chiller_1_T_CHW_out", "Chiller_1_T_CW_in", "Chiller_1_Load"
        );
        final List<String> totLabel = Arrays.asList(
                "Chiller_1_KW_tot" , "CWP_1_KW_tot" , "CWP_2_KW_tot" , "CWP_SP_KW_tot" ,
                "CHP_1_KW_tot" , "CHP_2_KW_tot" , "CHP_SP_KW_tot" , "CT_1_KW_tot" , "CT_2_KW_tot"
        );

        // ==========================================
        // ★★★ 3. 修改後的讀取邏輯 (重點在這裡) ★★★
        // ==========================================

        // 設定基礎資料夾路徑 (建議使用相對路徑 "datasets/"，這樣比較靈活)
        // 只要確保你的專案根目錄下有 datasets 資料夾即可
        String baseDir = "datasets/";

        // 1. 先嘗試組出「當前時間」的檔名 (例如 2026011322.csv)
        String targetFilename = year + month + day +  ".csv";
        String hourlyPath = baseDir + targetFilename;
        Path pathCheck = Paths.get(hourlyPath);

        // 2. 檢查該檔案是否存在
        if (!Files.exists(pathCheck)) {
            System.out.println("⚠️ 找不到當前時間的檔案: " + hourlyPath);

            // 3. 如果不存在，強制切換成「備援測試檔」 (請確保此檔案存在於 datasets 資料夾中)
            String defaultFile = "20260114.csv";
            hourlyPath = baseDir + defaultFile;

            System.out.println("🔄 自動切換讀取備援檔案: " + hourlyPath);

            // 4. 如果連備援檔都找不到，這時候再報錯
            if (!Files.exists(Paths.get(hourlyPath))) {
                throw new FileNotFoundException("❌ 嚴重錯誤：連備援檔案 (" + hourlyPath + ") 都找不到！請確認 datasets 資料夾內是否有 " + defaultFile);
            }
        }

        // 讀取資料
        List<Map<String, String>> hourData = CsvReader.read(hourlyPath);
        // ==========================================

        // 4. 呼叫 Control_predict 與 Get_kwh
        double ctemp = ControlPredict.run(hourData, labels);
        KwhResult kwhResult = KwhCalculator.calculate(hourData, kwLabel, totLabel, count);

        // 5. 溫度控制
        ctemp = TempControlRule.apply(ctemp);
        // updateTempConf(ctemp);

        // 6. 準備回傳資料
        Map<String, Object> ret = new LinkedHashMap<>();
        ret.put("Ntemp", 9.5);
        ret.put("Ptemp", 9.5);
        ret.put("kwh", kwhResult.getKwh());
        return ret;
    }
}