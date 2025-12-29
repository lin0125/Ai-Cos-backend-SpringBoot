package com.example.aicosbackendspringboot.dtos.request;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public class SelectModelRequest {

    private String type;
    private String filename;

    // Getter & Setter
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
}
