package com.example.aicosbackendspringboot.JWT.base.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.LinkedHashMap;

@ControllerAdvice
public class GlobaExceptionHeandler {

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception ex) {
        // 處理異常邏輯
        var response = new LinkedHashMap<String, Object>();
        response.put("status", "1");
        response.put("message", "失敗");
        response.put("失敗原因 : ", ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}