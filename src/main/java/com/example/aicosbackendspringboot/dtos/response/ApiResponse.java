package com.example.aicosbackendspringboot.dtos.response;

import lombok.Data;

@Data
public class ApiResponse {
    private String status;
    private Object data;
    private String error;
}
