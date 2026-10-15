package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 传感器监测数据对象
 * Created by zhujun on 2022/3/30.
 */
@Data
public class IotDeviceSensorCalculateVO implements Serializable {

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 距离
     */
    private String distance;

    /**
     * 物联属性信息
     */
    private List<SensorAttrMapVO> sensorAttrList;
}
