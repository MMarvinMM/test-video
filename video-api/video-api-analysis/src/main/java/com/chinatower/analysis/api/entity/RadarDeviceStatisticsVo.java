package com.chinatower.analysis.api.entity;

import lombok.Data;

/**
 * @Author: songxinfeng
 * @Date: 2023/11/8 14:41
 */
@Data
public class RadarDeviceStatisticsVo {

    /**
     * 雷达总数
     */
    private Integer radarDeviceCount;

    /**
     * 雷达在线数
     */
    private Integer radarDeviceOnlineCount;

    /**
     * 雷达离线数
     */
    private Integer radarDeviceOfflineCount;

    /**
     * 雷达在线率
     */
    private String radarDeviceOnlinePercent = "0";

}
