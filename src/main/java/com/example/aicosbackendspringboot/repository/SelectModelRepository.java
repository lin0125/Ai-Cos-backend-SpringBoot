package com.example.aicosbackendspringboot.repository;

import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Repository
public class SelectModelRepository {

    private final Map<String, Path> MODEL_DIRS = Map.of(
            "RT", Paths.get("./RT_model"),
            "PW", Paths.get("./PW_model")
    );

    private final List<String> ALLOWED_EXTS = List.of(".pickle", ".pkl");

    // 列出目錄下的模型檔案
    public List<String> listModelFiles(String kind) {
        try {
            return Files.list(MODEL_DIRS.get(kind))
                    .filter(p -> !Files.isDirectory(p) && ALLOWED_EXTS.contains(getExtension(p)))
                    .map(p -> p.getFileName().toString())
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Error reading model directory", e);
        }
    }

    // 寫入 ACTIVE.txt
    public void writeActiveFile(String kind, String filename) throws IOException {
        Path activePath = MODEL_DIRS.get(kind).resolve("ACTIVE.txt");
        Files.writeString(activePath, filename, StandardCharsets.UTF_8);
    }

    // 取得檔案副檔名
    private String getExtension(Path p) {
        String name = p.getFileName().toString().toLowerCase();
        int idx = name.lastIndexOf(".");
        return idx > 0 ? name.substring(idx) : "";
    }

    // 模擬重新載入模型的行為
    public boolean reloadModels() {
        // 這裡可依原 Python mm.reload_models 對應的功能進行實作
        return true;
    }
}
