package com.example.aicosbackendspringboot.tool;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CsvReader {
    public static List<Map<String, String>> read(String filePath) throws Exception {
        List<Map<String, String>> recordsList = new ArrayList<>();

        try (FileReader reader = new FileReader(Paths.get(filePath).toFile());
             CSVParser parser = CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .parse(reader)) {

            for (CSVRecord record : parser) {
                Map<String, String> row = new LinkedHashMap<>();
                for (String header : parser.getHeaderMap().keySet()) {
                    row.put(header, record.get(header));
                }
                recordsList.add(row);
            }
        }
        return recordsList;
    }
}
