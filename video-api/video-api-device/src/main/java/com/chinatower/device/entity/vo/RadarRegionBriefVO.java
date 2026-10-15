package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 雷达告警区域简要信息实体类
 */
@Data
public class RadarRegionBriefVO implements Serializable {


    /**
     * 告警区域ID
     */
    private Long regionId;

    /**
     * 告警区域名称
     */
    private String regionName;

    /**
     * 告警区域类型
     */
    private String regionType;

    /**
     * 地图标绘信息
     */
    private String regionJson;

}
