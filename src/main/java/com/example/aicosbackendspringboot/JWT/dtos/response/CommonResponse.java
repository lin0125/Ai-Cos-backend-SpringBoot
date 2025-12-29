package com.example.aicosbackendspringboot.JWT.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse {
    private boolean ok;
    private String error;
    private Object data;
}