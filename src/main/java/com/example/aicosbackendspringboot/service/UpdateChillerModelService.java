package com.example.aicosbackendspringboot.service;

import com.example.aicosbackendspringboot.repository.FileStorageRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class UpdateChillerModelService {
    private final Path rtModelPath;
    private final Path pwModelPath;
    private final FileStorageRepository fileStorageRepository;

    // 根據來源碼，定義允許的副檔名
    private static final List<String> ALLOWED_EXTS = List.of(".pickle", ".pkl"); // [T0](1)

    // 建議將路徑設定在 application.properties 中，此處為簡化範例
    public UpdateChillerModelService(FileStorageRepository fileStorageRepository) {
        this.fileStorageRepository = fileStorageRepository;
        // 模擬 Python 中的 BASE_DIR 和 MODEL_DIRS
        Path baseDir = Paths.get(System.getProperty("user.dir")); // [T0](1)
        this.rtModelPath = baseDir.resolve("RT_model"); // [T0](1)
        this.pwModelPath = baseDir.resolve("PW_model"); // [T0](1)
    }

    public Map<String, String> saveModel(MultipartFile file, String type) throws IOException {
        // 1. 驗證模型類型
        String modelType = type.toUpperCase();
        if (!"RT".equals(modelType) && !"PW".equals(modelType)) {
            throw new IllegalArgumentException("type must be RT or PW");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("empty filename");
        }

        // 2. 驗證副檔名
        String extension = "";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0) {
            extension = originalFilename.substring(i).toLowerCase();
        }
        if (!ALLOWED_EXTS.contains(extension)) { // [T0](1)
            throw new IllegalArgumentException("extension " + extension + " not allowed");
        }

        // 3. 產生儲存檔名 (時間戳 + 安全檔名)
        String safeName = StringUtils.cleanPath(originalFilename); // Spring 的工具類，類似 secure_filename
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now());
        String saveName = timestamp + "_" + safeName;

        // 4. 決定儲存路徑
        Path targetDir = "RT".equals(modelType) ? rtModelPath : pwModelPath;
        Path savePath = targetDir.resolve(saveName);

        // 5. 呼叫 Repository 層進行儲存
        fileStorageRepository.save(file, savePath);

        // 6. 回傳結果
        return Map.of(
                "status", "ok",
                "type", modelType,
                "saved", saveName
        );
    }
}
