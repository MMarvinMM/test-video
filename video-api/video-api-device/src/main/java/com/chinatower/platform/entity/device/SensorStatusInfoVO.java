package com.chinatower.platform.entity.device;

import lombok.Data;

import java.io.Serializable;

@Data
public class SensorStatusInfoVO implements Serializable {

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 传感器编码
     */
    private String sensorCode;

    /**
     * 物联因子编码
     */
    private String monitorCode;

    /**
     * 物联因子名称
     */
    private String monitorName;

    /**
     * 物联因子状态 1-在线 0-离线
     */
    private Integer monitorStatus;

    /**
     * 物联因子单位
     */
    private String monitorUnits;

    /**
     * 传感器类型编码
     */
    private String sensorTypeCode;

    /**
     * 传感器类型名称
     */
    private String sensorTypeName;

}
