package com.chinatower.device.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2025/1/16 17:25
 */
@Data
public class UavFlyTaskRecordReq {
    private String uavFlyId;
    /**
     * 飞行任务id
     */
    private String taskRecordId;

    /**
     * 航线id
     */
    private String flightRouteId;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 执行进度
     */
    private String process;

    /**
     * 任务状态
     */
    private Integer status;

    /**
     * 到达航点
     */
    private Integer arriveIdx;

    /**
     * 数据同步状态
     */
    private Integer dataSync;

    /**
     * 区域信息
     */
    private List<Integer> areaList;

    /**
     * 接入节点
     */
    private String accessNode;

    private String createTime;
}
