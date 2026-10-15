package com.chinatower.event.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class IotDeviceInfoVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 物联设备编号
     */
    private String deviceCode;

    /**
     * 物联设备名称
     */
    private String deviceName;

    /**
     * 物联设备厂家
     */
    private String modelFactoryName;

    /**
     * 物联设备型号
     */
    private String deviceModelCode;

    /**
     *设备地址
     */
    private String deviceAddress;

    /**
     * 摄像机厂家
     */
    private String modelId;

    /**
     * 设备挂高
     */
    private String height;

    /**
     * 设备维度
     */
    private String videoLatitude;

    /**
     * 设备经度
     */
    private String videoLongitude;

    /**
     * 设备经度
     */
    private String deviceLongitude;

    /**
     * 设备维度
     */
    private String deviceLatitude;

    /**
     * 摄像机焦距范围
     */
    private String focusRange;

    /**
     * 水平向下为负 0否1是
     */
    private String vertiDownNeg;

    /**
     * 联动摄像机信息
     */
    private List<IotRelDeviceVo> iotDeviceList;

    /**
     * 设备类型名称
     * @required true
     */
    private String deviceTypeName;
}
