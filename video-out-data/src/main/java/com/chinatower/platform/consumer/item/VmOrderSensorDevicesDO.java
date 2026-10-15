package com.chinatower.platform.consumer.item;

import lombok.Data;

@Data
public class VmOrderSensorDevicesDO {

    /**
     * 租户编码
     */
    private String tenantId;

    /**
     * 设备编号
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;
}
