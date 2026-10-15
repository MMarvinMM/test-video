package com.chinatower.analysis.api.entity;

import lombok.Data;

/**
 * @Author: songxinfeng
 * @Date: 2023/11/8 14:41
 */
@Data
public class IotDeviceStatisticsVo {

    /**
     * 物联总数
     */
    private Integer iotDeviceCount;

    /**
     * 物联在线数
     */
    private Integer iotDeviceOnlineCount;

    /**
     * 物联离线数
     */
    private Integer iotDeviceOfflineCount;

    /**
     * 物联在线率
     */
    private String iotDeviceOnlinePercent = "0";

}
