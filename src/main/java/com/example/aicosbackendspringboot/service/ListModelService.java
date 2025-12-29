package com.example.aicosbackendspringboot.service;

import com.example.aicosbackendspringboot.repository.ListModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ListModelService {

    @Autowired
    private ListModelRepository modelRepository;

    public Map<String, Object> listModels(String type) {
        String modelType = type.toUpperCase();
        if (!modelType.equals("RT") && !modelType.equals("PW")) {
            throw new IllegalArgumentException("type must be RT or PW");
        }
        List<String> files = modelRepository.listModelFiles(modelType);
        String active = modelRepository.getActiveFile(modelType);
        return Map.of("type", modelType, "active", active, "files", files);
    }
}
