package com.example.aicosbackendspringboot.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Repository
public class FileStorageRepository {
    /**
     * 將上傳檔案儲存到指定路徑
     * @param file MultipartFile 檔案
     * @param destinationPath 包含檔名的完整目標路徑
     * @throws IOException 寫入失敗時拋出
     */
    public void save(MultipartFile file, Path destinationPath) throws IOException {
        // 確保目標資料夾存在，類似 os.makedirs(..., exist_ok=True)
        Files.createDirectories(destinationPath.getParent()); // [T0](1)

        try (InputStream inputStream = file.getInputStream()) {
            // 執行檔案複製
            Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
