package com.chinatower.platform.entity.device;

import lombok.Data;

/**
 * @program: video-platform
 * @ClassName: DeviceTreeForestryVO
 * @description: 林业传参使用
 * @author: fengty
 * @create: 2024-08-13 18:30
 */
@Data
public class DeviceTreeForestryVO extends DeviceTreeVO{
    private String areaCode;

    private String areaName;

    private String areaLevel;

    private String areaParentCode;

    private String areaParentName;

    private String provinceId;

    private String cityId;

    private String countyId;
}
