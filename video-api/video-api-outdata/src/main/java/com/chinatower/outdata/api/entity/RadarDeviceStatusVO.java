package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RadarDeviceStatusVO implements Serializable {

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备状态
     */
    private Integer status;

    /**
     * 目标id
     */
    private String targetId;

    /**
     * 消息id
     */
    private String messageId;
}
