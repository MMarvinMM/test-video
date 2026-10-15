package com.chinatower.device.entity.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AlgorithmDto {
    /**
     * 算法编码集合
     */
    private List<String> algorithmCode ;
    /**
     * 电子围栏信息json
     */
    private UavMonitorGeoJson uavMonitorGeoJson;
    /**
     * 高级参数
     */
    private List<Map<String, Object>> superConfig;
    /**
     * 算法识别时间开关 0 1
     */
    private String isTimeOpen;
    /**
     * 算法开始时间
     */
    private String timeStart;
    /**
     * 算法结束时间
     */
    private String timeEnd;

}
