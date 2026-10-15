package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceAttrVO implements Serializable {
    /** 设备编码 */
    private String deviceCode;
    /** 设备名称 */
    private String deviceName;
    /** 设备状态 */
    private Integer ordStatus;
    /** 行业编码 */
    private String industryCode;
    /** 版本编码 */
    private String appVerCode;
}
