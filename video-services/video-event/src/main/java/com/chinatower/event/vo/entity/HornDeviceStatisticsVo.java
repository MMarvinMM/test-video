package com.chinatower.event.vo.entity;

import lombok.Data;

/**
 * @Author: songxinfeng
 * @Date: 2023/11/8 14:41
 */
@Data
public class HornDeviceStatisticsVo {

    /**
     * 喇叭总数
     */
    private Integer hornDeviceCount;

    /**
     * 喇叭工作数
     */
    private Integer hornDeviceWorkCount;

    /**
     * 喇叭离线数
     */
    private Integer hornDeviceOfflineCount;

    /**
     * 喇叭空闲数
     */
    private Integer hornDeviceFreeCount;

    /**
     * 喇叭故障数
     */
    private Integer hornDeviceFailCount;

    /**
     * 喇叭从未连接数
     */
    private Integer hornDeviceNoConnectCount;

    /**
     * 喇叭在线数
     */
    private Integer hornDeviceOnlineCount;

    /**
     * 喇叭在线率
     */
    private String hornDeviceOnlinePercent = "0";
}
