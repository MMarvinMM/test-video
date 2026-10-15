package com.chinatower.platform.entity.device;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageIotDeviceVO implements Serializable {

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 设备状态
     */
    private String deviceStatus;


}
