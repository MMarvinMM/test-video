package com.chinatower.analysis.api.lowalt.domain.vo;

import lombok.Data;

/**
 * 设备在线率VO
 */
@Data
public class DeviceOnlineRateVO {
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
     * 设备总数
     */
    private Integer totalCount;

    /**
     * 在线率
     */
    private Double onlineRate;

    /**
     * 所有设备在线率
     */
    private Double allDeviceOnlineRate;
}