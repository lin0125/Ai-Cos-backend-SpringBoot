package com.example.aicosbackendspringboot.JWT.service.impl;

import com.example.aicosbackendspringboot.JWT.service.GoogleAuthService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

@Service
public class GoogleAuthImpl implements GoogleAuthService {

    // 從 application.properties 中讀取設定好的 Client ID
    @Value("${google.web.client.id}")
    private String googleWebClientId;

    /**
     * 驗證 Google ID Token 並回傳 Payload。
     * @param idTokenString 從客戶端傳來的 ID Token 字串。
     * @return 如果 Token 有效，則回傳 Optional<GoogleIdToken.Payload>，否則回傳 Optional.empty()。
     */
    @Override
    public Optional<GoogleIdToken.Payload> verifyToken(String idTokenString) {
        // 初始化 Google ID Token 驗證器
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                // 指定這個 Token 是頒發給哪個 Client ID 的 (也就是你的後端)
                .setAudience(Collections.singletonList(googleWebClientId))
                .build();

        try {
            // 執行驗證
            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                // 驗證成功，回傳 Payload
                return Optional.of(idToken.getPayload());
            }
        } catch (GeneralSecurityException | IOException e) {
            // 處理驗證過程中可能發生的網路或安全例外
            // 可以在此處加入日誌記錄
            System.err.println("Token verification failed: " + e.getMessage());
        }

        // 驗證失敗或發生例外
        return Optional.empty();
    }
}
