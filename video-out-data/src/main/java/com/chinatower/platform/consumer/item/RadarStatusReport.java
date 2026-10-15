package com.chinatower.platform.consumer.item;

import lombok.Data;

import java.io.Serializable;

@Data
public class RadarStatusReport implements Serializable {

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备状态
     */
    private int deviceStatus;

    /**
     * 上报时间
     */
    private String updateTime;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 受理节点
     */
    private String acceptNode;

}
