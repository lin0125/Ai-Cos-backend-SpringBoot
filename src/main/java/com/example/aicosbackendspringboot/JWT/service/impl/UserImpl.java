package com.example.aicosbackendspringboot.JWT.service.impl;

import com.example.aicosbackendspringboot.JWT.config.JWTService;
import com.example.aicosbackendspringboot.JWT.dtos.request.LoginRequest;
import com.example.aicosbackendspringboot.JWT.repository.UserRepository;
import com.example.aicosbackendspringboot.JWT.dtos.response.CommonResponse;
import com.example.aicosbackendspringboot.JWT.service.GoogleAuthService;
import com.example.aicosbackendspringboot.JWT.service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.RequiredArgsConstructor;
import com.example.aicosbackendspringboot.JWT.base.Role;
import com.example.aicosbackendspringboot.JWT.entities.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserImpl implements UserService {

    private final UserRepository userRepository;
    private final GoogleAuthService googleAuthService;
    private final JWTService jwtService;

    @Value("${admin.email}")
    private List<String> adminEmail; // 移除 final 修正 @Value 注入問題

    @Override
    public CommonResponse authGoogleToken(LoginRequest loginRequest) {

        if (loginRequest == null || loginRequest.googleToken() == null || loginRequest.googleToken().isEmpty()) {
            return CommonResponse.builder()
                    .ok(false)
                    .error("ID Token is missing")
                    .build();
        }

        Optional<GoogleIdToken.Payload> payloadOptional = googleAuthService.verifyToken(loginRequest.googleToken());

        if (payloadOptional.isPresent()) {
            GoogleIdToken.Payload payload = payloadOptional.get();
            String googleId = payload.getSubject();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            UserEntity userToProcess;
            Optional<UserEntity> existingUser = userRepository.findByUserEmail(email);

            if (existingUser.isPresent()) {
                // 情境 A：使用者已存在資料庫 (由 Admin 添加或是已登錄過)
                userToProcess = existingUser.get();
            } else {
                // 情境 B：使用者不存在資料庫
                userToProcess = new UserEntity();
                userToProcess.setUserEmail(email);
                userToProcess.setUserName(name);
                userToProcess.setUserGoogleId(googleId);

                // 判斷是否為預設管理員
                if (adminEmail != null && adminEmail.contains(email)) {
                    userToProcess.setRole(Role.admin);
                    // 管理員第一次登入，自動存入資料庫以利後續查詢
                    userToProcess = userRepository.save(userToProcess);
                } else {
                    // 💡 關鍵修改：若不是管理員且不在資料庫名單中，分配為 other
                    // 這裡選擇不存入資料庫 (Transient)，或是存入但標記為 other
                    userToProcess.setRole(Role.other);
                    // 注意：如果您的 JWTService.generateToken 需要 ID，
                    // 這裡可能需要暫時產生一個 UserEntity 物件而不存檔
                }
            }

            // 產生包含 role (admin/user/other) 的 Token
            String userToken = jwtService.generateToken(userToProcess);

            LinkedHashMap<String, Object> data = new LinkedHashMap<>();
            data.put("message", "Authentication Successful");
            data.put("userName", name);
            data.put("userEmail", email);
            data.put("token", userToken);
            data.put("userRole", userToProcess.getRole());

            return CommonResponse.builder()
                    .ok(true)
                    .data(data)
                    .build();
        } else {
            return CommonResponse.builder()
                    .ok(false)
                    .error("Authentication failed: Invalid ID Token.")
                    .build();
        }
    }
}