package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SensorMonitorRelInfoVo implements Serializable {

    /**
     * 传感器名称
     */
    private String sensorName;

    /**
     * 传感器编码
     */
    private String sensorCode;

    /**
     * 传感器类型编码
     */
    private String sensorTypeCode;

    /**
     * 传感器类型名称
     */
    private String sensorTypeName;

    /**
     * 物联因子检测值在mongo数据里对应的key
     */
    private String valueKey;

    /**
     * 传感器后缀
     */
    private String suffix;

    /**
     * 物联因子编码
     */
    private String monitorCode;

    /**
     * 物联因子名称
     */
    private String monitorName;

    /**
     * 单位
     */
    private String monitorUnits;
}
