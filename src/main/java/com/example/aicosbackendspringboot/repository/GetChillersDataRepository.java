package com.example.aicosbackendspringboot.repository;

import com.example.aicosbackendspringboot.dtos.chiller.ChillerData;
import org.apache.commons.csv.CSVFormat;

import org.apache.commons.csv.CSVParser;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Repository
public class GetChillersDataRepository {

    private final String DEFAULT_CSV_PATH = "datasets/2024082018.csv";
    private final String HOURLY_DATA_DIR = "C:/Megabank_datafile/ITRI/Hourly/";

    /**
     * 讀取當前小時的冰機 CSV 資料
     */
    public ChillerData readHourlyChillerData() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minusHours(1);
        String year = String.valueOf(oneHourAgo.getYear());
        String month = String.format("%02d", oneHourAgo.getMonthValue());
        String day = String.format("%02d", oneHourAgo.getDayOfMonth());
        String hour = String.format("%02d", oneHourAgo.getHour());

        Path filePath = Paths.get(HOURLY_DATA_DIR + year + month + day + hour + ".csv");

        if (!Files.exists(filePath)) {
            filePath = Paths.get(DEFAULT_CSV_PATH);
        }

        try (Reader reader = Files.newBufferedReader(filePath)) {
            CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
            Map<String, Double> signalMean = new HashMap<>();
            Map<String, Double> tempMean = new HashMap<>();

            // 計算欄位平均值
            signalMean.put("Chiller_1_Signal", getColumnMean(parser, "Chiller_1_Signal"));
            signalMean.put("Chiller_2_Signal", getColumnMean(parser, "Chiller_2_Signal"));

            tempMean.put("Chiller_1_T_SP", getColumnMean(parser, "Chiller_1_T_SP"));
            tempMean.put("Chiller_2_T_SP", getColumnMean(parser, "Chiller_2_T_SP"));

            return new ChillerData(signalMean, tempMean);
        }
    }

    private double getColumnMean(CSVParser parser, String columnName) {
        return parser.getRecords().stream()
                .mapToDouble(record -> Double.parseDouble(record.get(columnName)))
                .average()
                .orElse(0.0);
    }
}
