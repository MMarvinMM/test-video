package com.chinatower.event.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 无人机告警来源vo类
 * @Author lize
 * @Date 2024/11/4 21:04
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UavAlarmSourceVo {

    /**
     * 无人机编号
     */
    private String deviceCode;

    /**
     * 无人机名称
     */
    private String deviceName;

    /**
     * 无人机厂家编码
     */
    private String manufacturerCode;

    /**
     * 无人机厂家
     */
    private String modelFactoryName;

    /**
     * 无人机型号
     */
    private String deviceModelCode;

    /**
     * 无人机地址
     */
    private String location;

    /**
     * 无人机经度
     */
    private String longitude;

    /**
     * 无人机纬度
     */
    private String latitude;

    /**
     * 置信度
     */
    private String confidenceLevel;

    /**
     * 机场
     */
    private List<UavNestDeviceVo> uavNestDeviceList;
}
