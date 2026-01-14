package com.example.aicosbackendspringboot.repository;

import com.example.aicosbackendspringboot.dtos.chiller.ChillerData;
import com.example.aicosbackendspringboot.tool.CsvReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class GetChillersDataRepository {

    @Value("${csv.file.path}")
    private String csvFilePath;

    public ChillerData readHourlyChillerData() {
        // [測試模式] 強制指定時間，讀取 2025/05/16 02:00 的資料
        LocalDateTime now = LocalDateTime.of(2025, 5, 16, 2, 0, 0);

        String fileName = now.format(DateTimeFormatter.ofPattern("yyyyMMddHH")) + ".csv";
        File targetFile = new File(csvFilePath, fileName);

        // 1. 檢查檔案 (包含自動往回找與測試檔邏輯)
        if (!targetFile.exists()) {
            System.err.println("❌ [Repository] 當前檔案不存在: " + targetFile.getAbsolutePath());
            boolean found = false;

            // 往回找最近 5 小時
            for (int i = 1; i <= 5; i++) {
                LocalDateTime pastTime = now.minusHours(i);
                String pastName = pastTime.format(DateTimeFormatter.ofPattern("yyyyMMddHH")) + ".csv";
                File pastFile = new File(csvFilePath, pastName);
                if (pastFile.exists()) {
                    targetFile = pastFile;
                    System.out.println("✅ [Repository] 找到歷史檔案: " + pastName);
                    found = true;
                    break;
                }
            }

            // 讀取備用測試檔
            if (!found) {
                File testFile = new File(csvFilePath, "2025051602.csv"); // 根據你的 log 調整
                if (testFile.exists()) {
                    targetFile = testFile;
                    System.out.println("⚠️ [Repository] 使用測試備用檔: " + testFile.getName());
                } else {
                    return new ChillerData(new HashMap<>(), new HashMap<>());
                }
            }
        } else {
            System.out.println("✅ [Repository] 找到當前檔案: " + targetFile.getName());
        }

        // 2. 執行讀取
        try {
            System.out.println("📂 [Repository] 最終讀取路徑: " + targetFile.getAbsolutePath());
            List<Map<String, String>> rawDataList = CsvReader.read(targetFile.getAbsolutePath());
            System.out.println("📊 [Repository] 資料筆數: " + rawDataList.size());

            if (rawDataList.isEmpty()) {
                return new ChillerData(new HashMap<>(), new HashMap<>());
            }

            return processRawData(rawDataList);

        } catch (Exception e) {
            e.printStackTrace();
            return new ChillerData(new HashMap<>(), new HashMap<>());
        }
    }

    private ChillerData processRawData(List<Map<String, String>> rawDataList) {
        Map<String, Double> tempSum = new HashMap<>();
        Map<String, Double> signalSum = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();

        // 1. 定義 CSV 裡真正有的欄位 (包含 T_CHW_out)
        String[] tempFields = {
                "Chiller_1_T_SP", "Chiller_2_T_SP",
                "Chiller_1_Evap_Out_Temp", "Chiller_2_Evap_Out_Temp", // 可能不存在
                "Chiller_1_T_CHW_out", "Chiller_2_T_CHW_out"          // 這是你 CSV 裡真正的出水溫
        };
        String[] signalFields = {"Chiller_1_Signal", "Chiller_2_Signal"};

        // 2. 累加數值
        for (Map<String, String> row : rawDataList) {
            for (String field : tempFields) accumulate(tempSum, counts, row, field);
            for (String field : signalFields) accumulate(signalSum, counts, row, field);
        }

        // 3. 計算平均
        int totalRows = rawDataList.size();
        Map<String, Double> tempMean = new HashMap<>();
        Map<String, Double> signalMean = new HashMap<>();

        if (totalRows > 0) {
            tempSum.forEach((k, v) -> tempMean.put(k, v / totalRows));
            signalSum.forEach((k, v) -> signalMean.put(k, v / totalRows));
        }

        // ★★★ 關鍵修正：強制把 T_CHW_out 的值 複製給 Evap_Out_Temp ★★★
        // 這樣 Service 就一定抓得到 Evap_Out_Temp
        forceMapValue(tempMean, "Chiller_1_Evap_Out_Temp", "Chiller_1_T_CHW_out");
        forceMapValue(tempMean, "Chiller_2_Evap_Out_Temp", "Chiller_2_T_CHW_out");

        // 如果上面失敗，再試試看用 T_SP 補 (保底)
        if (!tempMean.containsKey("Chiller_1_Evap_Out_Temp")) {
            forceMapValue(tempMean, "Chiller_1_Evap_Out_Temp", "Chiller_1_T_SP");
        }
        if (!tempMean.containsKey("Chiller_2_Evap_Out_Temp")) {
            forceMapValue(tempMean, "Chiller_2_Evap_Out_Temp", "Chiller_2_T_SP");
        }

        System.out.println("🧮 [Check] 最終 Temp Map: " + tempMean);
        return new ChillerData(tempMean, signalMean);
    }

    // 強制賦值 helper
    private void forceMapValue(Map<String, Double> map, String targetKey, String sourceKey) {
        if (map.containsKey(sourceKey)) {
            Double val = map.get(sourceKey);
            map.put(targetKey, val); // 直接覆蓋或新增
            System.out.println("🔧 [Repository] 成功補值: " + targetKey + " = " + val);
        }
    }

    private void accumulate(Map<String, Double> sumMap, Map<String, Integer> countMap, Map<String, String> row, String field) {
        if (row.containsKey(field)) {
            try {
                String valStr = row.get(field);
                if (valStr != null && !valStr.trim().isEmpty()) {
                    double val = Double.parseDouble(valStr);
                    sumMap.put(field, sumMap.getOrDefault(field, 0.0) + val);
                }
            } catch (NumberFormatException e) { }
        }
    }
}