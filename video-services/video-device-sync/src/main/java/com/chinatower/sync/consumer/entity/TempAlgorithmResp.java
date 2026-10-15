package com.chinatower.sync.consumer.entity;

import lombok.Data;

/**
 * @Author: songxinfeng
 * @Date: 2025/1/15 16:36
 */

@Data
public class TempAlgorithmResp {

    /**
     * 航线id
     */
    private String flightRouteId;

    /**
     * 算法编码
     */
    private String warningTypeId;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 受理节点
     */
    private String acceptNode;

    /**
     * 航线算法类型
     */
    private String algorithmType;

    /**
     * 设备编码
     */
    private String deviceCode;

    private String taskId;
}
