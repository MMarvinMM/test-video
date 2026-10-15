package com.chinatower.sync.consumer.item;

import lombok.Data;

import java.io.Serializable;

/**
 * 4.25.设备状态同步-到其他系统-队列推送 请求实体类
 */
@Data
public class AIotDeviceChannelStatus implements Serializable{

    /**
     * 通道状态，0离线，1在线，2未配置设备
     */
    private int channelStatus;

    /**
     * 通道编码，国标编码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

}