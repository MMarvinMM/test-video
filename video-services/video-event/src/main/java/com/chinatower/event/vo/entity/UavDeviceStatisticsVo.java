package com.chinatower.event.vo.entity;

import lombok.Data;

/**
 * @Author: songxinfeng
 * @Date: 2023/11/8 14:41
 */
@Data
public class UavDeviceStatisticsVo {

    /**
     * 无人机总数
     */
    private Integer uavDeviceCount;

    /**
     * 无人机在线数
     */
    private Integer uavDeviceOnlineCount;

    /**
     * 无人机离线数
     */
    private Integer uavDeviceOfflineCount;

    /**
     * 无人机在线率
     */
    private String uavDeviceOnlinePercent = "0";

    /**
     * 无人机通道总数
     */
    private Integer uavChannelCount;

    /**
     * 无人机通道在线数
     */
    private Integer uavChannelOnlineCount;

    /**
     * 无人机通道在线率
     */
    private String uavChannelOnlinePercent = "0";

    /**
     * 无人机通道离线数
     */
    private Integer uavChannelOfflineCount;


    /**
     * 机场总数
     */
    private Integer uavNestDeviceCount;

    /**
     * 机场在线数
     */
    private Integer uavNestDeviceOnlineCount;

    /**
     * 机场离线数
     */
    private Integer uavNestDeviceOfflineCount;

    /**
     * 机场在线率
     */
    private String uavNestDeviceOnlinePercent = "0";
}
