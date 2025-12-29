package com.example.aicosbackendspringboot.dtos.chiller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChillerConfig {

    @JsonProperty("min_temperature")
    private Double min_temperature;

    @JsonProperty("max_temperature")
    private Double max_temperature;

    @JsonProperty("base_temperature")
    private Double base_temperature;

    @JsonProperty("temp_step")
    private Double temp_step;

    @JsonProperty("update_rate")
    private Integer update_rate;
}
