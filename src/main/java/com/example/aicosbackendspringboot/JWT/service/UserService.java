package com.example.aicosbackendspringboot.JWT.service;

import com.example.aicosbackendspringboot.JWT.dtos.request.LoginRequest;
import com.example.aicosbackendspringboot.JWT.dtos.response.CommonResponse;

public interface UserService {
    CommonResponse authGoogleToken(LoginRequest request);
}
