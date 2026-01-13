package com.example.aicosbackendspringboot.repository;

import com.example.aicosbackendspringboot.dtos.chiller.ChillerData;
import com.example.aicosbackendspringboot.tool.CsvReader;
import org.apache.commons.csv.CSVFormat;

import org.apache.commons.csv.CSVParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        LocalDateTime now = LocalDateTime.now();
        String fileName = now.format(DateTimeFormatter.ofPattern("yyyyMMddHH")) + ".csv";

        File file = new File(csvFilePath, fileName);

        System.out.println("========================================");
        System.out.println("🔍 [Repository] 準備讀取冰機 CSV 檔案...");
        System.out.println("📂 [Repository] 目標路徑: " + file.getAbsolutePath());

        // 1. 檢查檔案是否存在
        if (!file.exists()) {
            System.err.println("❌ [Repository] 檔案不存在！回傳空資料。");
            return new ChillerData(new HashMap<>(), new HashMap<>());
        }

        try {
            // 2. [修正點] 讀取原始資料 (這是 List<Map<String, String>>)
            List<Map<String, String>> rawDataList = CsvReader.read(file.getAbsolutePath());

            System.out.println("📊 [Repository] CSV 讀取完成，共 " + rawDataList.size() + " 筆數據");

            if (rawDataList.isEmpty()) {
                return new ChillerData(new HashMap<>(), new HashMap<>());
            }

            // 3. [新增] 將原始 CSV 資料轉換成 ChillerData (計算平均值)
            return processRawData(rawDataList);

        } catch (Exception e) {
            System.err.println("❌ [Repository] CSV 讀取發生錯誤: " + e.getMessage());
            e.printStackTrace();
            return new ChillerData(new HashMap<>(), new HashMap<>());
        }
    }

    // 這是用來計算平均值的小工具方法
    private ChillerData processRawData(List<Map<String, String>> rawDataList) {
        Map<String, Double> tempSum = new HashMap<>();
        Map<String, Double> signalSum = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();

        // 定義我們要抓取的欄位名稱
        String[] tempFields = {"Chiller_1_T_SP", "Chiller_2_T_SP", "Chiller_1_Evap_Out_Temp", "Chiller_2_Evap_Out_Temp"};
        String[] signalFields = {"Chiller_1_Signal", "Chiller_2_Signal"};

        for (Map<String, String> row : rawDataList) {
            // 累加溫度
            for (String field : tempFields) {
                accumulate(tempSum, counts, row, field);
            }
            // 累加訊號
            for (String field : signalFields) {
                accumulate(signalSum, counts, row, field); // 訊號共用 counts 計算平均其實沒差，因為每一行都有
            }
        }

        int totalRows = rawDataList.size();
        Map<String, Double> tempMean = new HashMap<>();
        Map<String, Double> signalMean = new HashMap<>();

        // 計算平均 = 總和 / 行數
        tempSum.forEach((k, v) -> tempMean.put(k, v / totalRows));
        signalSum.forEach((k, v) -> signalMean.put(k, v / totalRows));

        // 回傳計算好的物件
        return new ChillerData(tempMean, signalMean);
    }

    private void accumulate(Map<String, Double> sumMap, Map<String, Integer> countMap, Map<String, String> row, String field) {
        if (row.containsKey(field)) {
            try {
                double val = Double.parseDouble(row.get(field));
                sumMap.put(field, sumMap.getOrDefault(field, 0.0) + val);
            } catch (NumberFormatException e) {
                // 忽略非數字的壞資料
            }
        }
    }
}