package com.chinatower.device.entity.vo;

import com.chinatower.device.entity.dto.UavMonitorGeoJson;
import lombok.Data;

import java.util.List;

@Data
public class UavInfo {
    private UavMonitorGeoJson uavMonitorGeoJson;
    private UavCamera uavCamera;
    private boolean mergeHeading;
    private boolean mergePitch;
    private boolean mergeRoll;
    private String flightRoutId;
    private String algorithmCode;
    private String delFlag;
    private String geometries;
    private String geometriesBuffer;

    @Data
    public static class UavCamera{
        private List<String> sensorSize;
    }
}
