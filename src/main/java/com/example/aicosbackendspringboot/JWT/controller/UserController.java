package com.example.aicosbackendspringboot.JWT.controller;

import com.example.aicosbackendspringboot.JWT.dtos.request.LoginRequest;
import com.example.aicosbackendspringboot.JWT.dtos.response.CommonResponse;
import com.example.aicosbackendspringboot.JWT.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "User Authentication", description = "使用者登入相關介面")
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    @Operation(summary = "Google 登入驗證", description = "接收 Google ID Token 並回傳 JWT")
    @PostMapping("/auth/google")
    private ResponseEntity<CommonResponse> authGoogleToken(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok().body(userService.authGoogleToken(request));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new CommonResponse(false, e.getMessage(), null));
        }
    }

}