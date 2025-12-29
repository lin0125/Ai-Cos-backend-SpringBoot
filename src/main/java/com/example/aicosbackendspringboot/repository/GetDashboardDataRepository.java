package com.example.aicosbackendspringboot.repository;

import com.example.aicosbackendspringboot.tool.*;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
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
        String conditionHour = String.format("%02d", count - 1);

        // 2. 判斷 output_state
        Path outputStatePath = Paths.get("output_state.txt");
        String outputPath;
        try (BufferedReader br = Files.newBufferedReader(outputStatePath)) {
            String line = br.readLine();
            if ("0".equals(line)) {
                outputPath = "output.csv";
            } else {
                outputPath = "C:/Megabank_datafile/SoundAir/AI-" + year + month + day + "-" + hour + ".csv";
            }
        }

        // labels / kwLabel / totLabel 定義這些欄位名稱
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

        // 3. 讀取 Hourly 資料
        String hourlyPath = "C:/Megabank_datafile/ITRI/Hourly/" + year + month + day + hour + ".csv";
        List<Map<String, String>> hourData = CsvReader.read(hourlyPath);// 需自行實作 DataFrame 封裝或使用第三方庫

        // 4. 呼叫 Control_predict 與 Get_kwh（需將 Python 邏輯移植至 Java）
        double ctemp = ControlPredict.run(hourData, labels);
        KwhResult kwhResult = KwhCalculator.calculate(hourData, kwLabel, totLabel, count);

        // 5. 溫度控制與檔案更新


        ctemp = TempControlRule.apply(ctemp);
//        updateTempConf(ctemp); // 寫入 temp_conf.txt 與 output.csv

        // 6. 準備回傳資料
        Map<String, Object> ret = new LinkedHashMap<>();
        ret.put("Ntemp", 9.5); // 依 chiller 狀態填入
        ret.put("Ptemp", 9.5);
        ret.put("kwh", kwhResult.getKwh());
        return ret;
    }
}
