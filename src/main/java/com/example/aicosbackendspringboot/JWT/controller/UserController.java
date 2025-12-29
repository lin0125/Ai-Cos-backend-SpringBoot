package com.example.aicosbackendspringboot.JWT.controller;

import com.example.aicosbackendspringboot.JWT.dtos.request.LoginRequest;
import com.example.aicosbackendspringboot.JWT.dtos.response.CommonResponse;
import com.example.aicosbackendspringboot.JWT.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/google")
    private ResponseEntity<CommonResponse> authGoogleToken(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok().body(userService.authGoogleToken(request));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new CommonResponse(false, e.getMessage(), null));
        }
    }

}