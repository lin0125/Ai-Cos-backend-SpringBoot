package com.example.aicosbackendspringboot.service;

import com.example.aicosbackendspringboot.dtos.chiller.ChillerConfig;
import com.example.aicosbackendspringboot.repository.UpdateChillerParamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UpdateChillerParamService {


    @Autowired
    private UpdateChillerParamRepository chillerRepository;

    public void updateChillerConfig(ChillerConfig dto) throws Exception {
        chillerRepository.saveConfig(dto);

    }
}
