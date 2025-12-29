package com.example.aicosbackendspringboot.JWT.service.impl;

import com.example.aicosbackendspringboot.JWT.dtos.dto.FieldData;
import com.example.aicosbackendspringboot.JWT.dtos.request.AddFieldRequest;
import com.example.aicosbackendspringboot.JWT.entities.FieldEntity;
import com.example.aicosbackendspringboot.JWT.repository.FieldRepository;
import com.example.aicosbackendspringboot.JWT.dtos.response.CommonResponse;
import com.example.aicosbackendspringboot.JWT.service.FieldService;
import lombok.RequiredArgsConstructor;
import com.example.aicosbackendspringboot.JWT.entities.UserEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FieldImpl implements FieldService {

    private final FieldRepository fieldRepository;

    @Override
    public CommonResponse addField(AddFieldRequest addFieldRequest) {
        var user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (addFieldRequest.fieldName().isEmpty() || addFieldRequest.fieldAddress().isEmpty()) {
            return CommonResponse.builder()
                    .ok(false)
                    .error("註冊的場域名稱或地址為空")
                    .build();
        }

        if (addFieldRequest.fieldContractEndTime() ==  null) {
            return CommonResponse.builder()
                    .ok(false)
                    .error("註冊的場域結束合約時間為空")
                    .build();
        }

        if (addFieldRequest.fieldContractEndTime().isBefore(LocalDateTime.now())) {
            return CommonResponse.builder()
                    .ok(false)
                    .error("註冊的場域結束合約時間有問題，不可在現在時間之前")
                    .build();
        }

        Optional<FieldEntity> fieldEntity = fieldRepository.getFieldEntityByBuildFieldEmailAndFieldName(user, addFieldRequest.fieldName());

        if (fieldEntity.isPresent()) {
            return CommonResponse.builder()
                    .ok(false)
                    .error("場域重複建立")
                    .build();
        } else {
            FieldEntity newFieldEntity = new FieldEntity();
            newFieldEntity.setFieldName(addFieldRequest.fieldName());
            newFieldEntity.setBuildFieldEmail(user);
            newFieldEntity.setBuildFieldAddress(addFieldRequest.fieldAddress());
            newFieldEntity.setFieldContractEndTime(addFieldRequest.fieldContractEndTime());

            fieldRepository.save(newFieldEntity);
        }

        LinkedHashMap<String, String> data = new LinkedHashMap<>();
        data.put("message", "場域建立成功");

        return CommonResponse.builder()
                .ok(true)
                .data(data)
                .build();
    }

    @Override
    public CommonResponse getField() {
        var user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<FieldEntity> fieldEntitiesList = user.getFieldEntity();

        List<FieldData> fieldDataList = fieldEntitiesList
                .stream()
                .map(fieldEntity -> {
                    LocalDateTime endTime = fieldEntity.getFieldContractEndTime();
                    String formattedTime = null; // 預設為 null

                    if (endTime != null) {
                        formattedTime = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    }
                    return new FieldData(
                            fieldEntity.getFieldName(),
                            formattedTime
                    );
                }).toList();

        return CommonResponse.builder()
                .ok(true)
                .data(fieldDataList)
                .build();
    }
}