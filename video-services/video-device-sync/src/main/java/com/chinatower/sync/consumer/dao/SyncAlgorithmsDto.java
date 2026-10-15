package com.chinatower.sync.consumer.dao;

import lombok.Data;

import java.io.Serializable;

/**
 * @author WLF
 * @description
 * @date 2024/11/7 18:48
 */
@Data
public class SyncAlgorithmsDto implements Serializable {
    // 航线id
    private String flightRouteId;

    private String deviceName;
    // 设备编码
    private String deviceCode;

    private String channelName;

    // 通道编码
    private String channelCode;

    // 算法编码通道任务此字段必填
    private String algorithmCode;

    // 算法任务id
    private String taskId;

    // 当前任务配置的航点信息
    private String arriveIdx;

    // 无人机配置的区域ID
    private String areaId;

    // 接入节点
    private String accessNode;

    private String acceptNode;

    // 分析类型
    private String analyType;

    // 操作类型
    private String optType;

    // 创建时间
    private String tenantCode;

    // 修改时间
    private String tenantName;

    // 版本号
    private Long taskVersion;

    private String createUser;

    private String teanantId;

    private String industryCode;

    /**
     * 开启状态（0：开启  1：关闭）
     */
    private String status;

    /**
     * 算法任务类型（1：航线正常算法 2：航线临时算法
     */
    private String algorithmType;

    /**
     * 恢复标记位（1：待恢复 其他：无需恢复）
     */
    private String recoveryFlag;
}
