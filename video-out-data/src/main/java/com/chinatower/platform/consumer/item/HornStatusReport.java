package com.chinatower.platform.consumer.item;

import lombok.Data;

import java.io.Serializable;

@Data
public class HornStatusReport implements Serializable {

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 在线状态
     *  0离线
     *  1空闲（在线）
     *  2工作中（在线）
     *  3故障（在线）
     */
    private Integer onlineStatus;

    /**
     * 监测时间
     */
    private String monitorTime;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 受理节点
     */
    private String acceptNode;
}
