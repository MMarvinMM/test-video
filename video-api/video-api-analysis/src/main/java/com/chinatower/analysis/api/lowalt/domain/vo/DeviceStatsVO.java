package com.chinatower.analysis.api.lowalt.domain.vo;

import lombok.Data;

/**
 * 设备统计VO
 */
@Data
public class DeviceStatsVO {

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 设备类型名称
     */
    private String deviceTypeName;

    /**
     * 在线数量
     */
    private Integer onlineCount;

    /**
     * 离线数量
     */
    private Integer offlineCount;

    /**
     * 告警数量
     */
    private Integer warningCount;

    /**
     * 设备总数
     */
    private Integer totalCount;
}