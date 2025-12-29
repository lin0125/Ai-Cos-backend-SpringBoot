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
    private final List<String> adminEmail;

    @Override
    public CommonResponse authGoogleToken(LoginRequest loginRequest) {

        if (loginRequest == null || loginRequest.googleToken().isEmpty()) {
            return CommonResponse.builder()
                    .ok(false)
                    .error("ID Token is missing")
                    .build();
        }

        Optional<GoogleIdToken.Payload> payloadOptional = googleAuthService.verifyToken(loginRequest.googleToken());

        if (payloadOptional.isPresent()) {
            // 驗證成功
            GoogleIdToken.Payload payload = payloadOptional.get();

            String googleId = payload.getSubject();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            UserEntity userToProcess;

            Optional<UserEntity> existingUser = userRepository.findByUserEmail(email);
            if (existingUser.isEmpty()) {
                // 使用者不存在，建立新使用者
                UserEntity newUser = new UserEntity();
                newUser.setUserEmail(email);
                newUser.setUserName(name);
                newUser.setUserGoogleId(googleId);

                if (adminEmail.contains(email)) {
                    newUser.setRole(Role.admin);
                } else {
                    newUser.setRole(Role.user);
                }

                userToProcess = userRepository.save(newUser);
            } else {
                userToProcess = existingUser.get();
            }

            String userToken = jwtService.generateToken(userToProcess);

            LinkedHashMap<String, Object> data = new LinkedHashMap<>();
            data.put("message", "Authentication Successful");
            data.put("userName", name);
            data.put("userEmail", email);
            data.put("userToken", userToken);
            data.put("userRole", userToProcess.getRole());

            return CommonResponse.builder()
                    .ok(true)
                    .data(data)
                    .build();
        } else {
            // 驗證失敗
            return CommonResponse.builder()
                    .ok(false)
                    .error("Authentication failed: Invalid ID Token.")
                    .build();
        }
    }
}
