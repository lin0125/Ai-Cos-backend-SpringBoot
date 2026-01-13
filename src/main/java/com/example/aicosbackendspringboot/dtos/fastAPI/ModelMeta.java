package com.example.aicosbackendspringboot.dtos.fastAPI;

public record ModelMeta(
        Integer id,
        String name,
        String version,
        String category,
        boolean is_active
) {}