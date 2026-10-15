package com.chinatower.system.api.model;

import lombok.Data;

/**
 * @desription: 设备
 * @data 创建时间：2023/8/22 9:36
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class DeviceTypeAndCodeVO {
    /**
     * 设备编码
     */
    private String deviceCode;
    /**
     * 设备类型
     */
    private String deviceType;

    public DeviceTypeAndCodeVO(String deviceCode, String deviceType) {
        this.deviceCode = deviceCode;
        this.deviceType = deviceType;
    }

    public DeviceTypeAndCodeVO() {
    }
}
