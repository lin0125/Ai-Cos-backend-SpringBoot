package com.example.aicosbackendspringboot.JWT.service;

import com.example.aicosbackendspringboot.JWT.dtos.request.AddFieldRequest;
import com.example.aicosbackendspringboot.JWT.dtos.response.CommonResponse;

public interface FieldService {
    CommonResponse addField(AddFieldRequest addFieldRequest);

    CommonResponse getField();
}
