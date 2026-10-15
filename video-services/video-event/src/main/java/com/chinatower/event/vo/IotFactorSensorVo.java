package com.chinatower.event.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class IotFactorSensorVo implements Serializable {

    /**
     * 传感器类型
     */
    private String sensorType;

    /**
     * 传感器类型名称
     */
    private String sensorTypeName;

    /**
     * 传感器
     */
    private String sensorCode;

    /**
     * 传感器名称
     */
    private String sensorName;

    /**
     * 传感器类型下的因子
     */
    private List<IotFactorVo> factorVoList;
}
