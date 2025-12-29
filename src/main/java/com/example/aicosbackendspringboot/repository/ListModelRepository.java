package com.example.aicosbackendspringboot.repository;

import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Repository
public class ListModelRepository {

    private final Map<String, Path> MODEL_DIRS = Map.of(
            "RT", Paths.get("./RT_model"),
            "PW", Paths.get("./PW_model")
    );

    public List<String> listModelFiles(String kind) {
        try {
            return Files.list(MODEL_DIRS.get(kind))
                    .filter(p -> !Files.isDirectory(p) && p.toString().endsWith(".pickle"))
                    .map(p -> p.getFileName().toString())
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Error reading model directory", e);
        }
    }

    public String getActiveFile(String kind) {
        Path activePath = MODEL_DIRS.get(kind).resolve("ACTIVE.txt");
        if (Files.exists(activePath)) {
            try {
                String content = Files.readString(activePath).trim();
                return content.isEmpty() ? null : content;
            } catch (IOException e) {
                throw new RuntimeException("Error reading ACTIVE.txt", e);
            }
        }
        return null;
    }
}
