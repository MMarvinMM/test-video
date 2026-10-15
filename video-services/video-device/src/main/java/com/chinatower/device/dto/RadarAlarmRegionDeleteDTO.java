package com.chinatower.device.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RadarAlarmRegionDeleteDTO implements Serializable {

    /**
     * 告警区域ID
     */
    private Long regionId;
}
