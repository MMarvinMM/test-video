package com.chinatower.outdata.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Preset {
    private Double pan;

    private Integer presetId;

    private Double tilt;

    private Double zoom;
}
