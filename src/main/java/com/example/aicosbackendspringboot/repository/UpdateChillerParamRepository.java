package com.example.aicosbackendspringboot.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.aicosbackendspringboot.dtos.chiller.ChillerConfig;
import org.springframework.stereotype.Repository;

import java.io.File;

@Repository
public class UpdateChillerParamRepository {
    private static final String CONFIG_PATH = "Chiller_config.json";

    public void saveConfig(ChillerConfig dto) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(CONFIG_PATH), dto);
    }
}
