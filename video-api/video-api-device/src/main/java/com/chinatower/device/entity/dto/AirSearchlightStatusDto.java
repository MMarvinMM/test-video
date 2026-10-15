package com.chinatower.device.entity.dto;

import lombok.Data;

/**
 * @author cuibaoluo
 * @date 2025/12/17
 * @description：
 */
@Data
public class AirSearchlightStatusDto {

    /**
     * 机巢编码
     */
    private String uavNestCode;

    /**
     * 探照灯编码
     */
    private String mountCode;

    /**
     * 探照灯开关
     */
    private String mountStatus;

    /**
     * 机巢编码 (底层字段)
     */
    private String deviceCode;
}
