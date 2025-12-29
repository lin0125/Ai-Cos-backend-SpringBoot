package com.example.aicosbackendspringboot.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.aicosbackendspringboot.dtos.chiller.ChillerConfig;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.Optional;

@Repository
public class GetChillerParamRepository {
    private static final String CONFIG_PATH = "Chiller_config.json";

    public Optional<ChillerConfig> findById(String chillerId) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ChillerConfig param = mapper.readValue(new File(CONFIG_PATH), ChillerConfig.class);
            // 可依 chillerId 做不同邏輯，這裡假設唯一檔案
            return Optional.of(param);
        } catch(Exception e) {
            return Optional.empty();
        }
    }
}
