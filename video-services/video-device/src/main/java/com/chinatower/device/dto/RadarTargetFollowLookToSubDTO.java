package com.chinatower.device.dto;

import lombok.Data;

import java.util.List;

@Data
public class RadarTargetFollowLookToSubDTO {

    private List<String> success;

    private List<String> fail;
}
