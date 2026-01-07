package com.example.aicosbackendspringboot.dtos.request;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public class SelectModelRequest {

    private String type;
    private Integer modelId; // 將 String filename 改為 Integer modelId

    // Getter & Setter
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getModelId() { return modelId; } // 對應修改
    public void setModelId(Integer modelId) { this.modelId = modelId; } // 對應修改
}
