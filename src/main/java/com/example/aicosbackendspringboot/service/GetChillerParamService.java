package com.example.aicosbackendspringboot.service;

import com.example.aicosbackendspringboot.dtos.chiller.ChillerConfig;
import com.example.aicosbackendspringboot.repository.GetChillerParamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetChillerParamService {
    @Autowired
    private GetChillerParamRepository chillerRepository;

    public Optional<ChillerConfig> getChillerParam(String chillerId) {
        return chillerRepository.findById(chillerId);
    }
}
