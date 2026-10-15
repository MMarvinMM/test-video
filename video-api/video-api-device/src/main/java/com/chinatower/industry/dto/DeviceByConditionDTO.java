package com.chinatower.industry.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceByConditionDTO implements Serializable {

    private String deviceCode;

    private String deviceName;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 设备状态:0在线，1离线
     */
    private String deviceStatus;

    /**
     * 设备类型:1-IPC 0-NVR;2-AR摄像头；3-热成像摄像头
     */
    private String deviceType;

    /**
     * 是否收藏 0未收藏 1已收藏
     */
    private String isMonitor;
}
