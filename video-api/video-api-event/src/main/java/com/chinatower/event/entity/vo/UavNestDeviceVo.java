package com.chinatower.event.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 机场
 * @Author lize
 * @Date 2024/11/4 21:09
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UavNestDeviceVo {

    /**
     * 设备编号
     */
    private String deviceCode;

    /**
     * 机场编号
     */
    private String uavNestCode;

    /**
     * 机场名称
     */
    private String uavNestName;

    /**
     * 机场厂家编码
     */
    private String modelCode;

    /**
     * 机场厂家
     */
    private String modelCodeName;

    /**
     * 机场型号
     */
    private String deviceModel;

    /**
     * 无人机经度
     */
    private String uaLongitude;

    /**
     * 无人机纬度
     */
    private String uaLatitude;
}

