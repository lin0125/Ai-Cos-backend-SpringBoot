package com.example.aicosbackendspringboot.dtos.chiller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChillerConfig {
    @JsonProperty("min_temperature")
    private Double minTemperature;

    @JsonProperty("max_temperature")
    private Double maxTemperature;

    @JsonProperty("base_temperature")
    private Double baseTemperature;

    @JsonProperty("temp_step")
    private Double tempStep;

    @JsonProperty("update_rate")
    private Integer updateRate;
}