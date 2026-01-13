package com.example.aicosbackendspringboot.dtos.fastAPI;

public record SwitchModelRequest(
        Integer rt_model_id,
        Integer pw_model_id
) {}