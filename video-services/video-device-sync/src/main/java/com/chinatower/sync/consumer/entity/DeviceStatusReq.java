package com.chinatower.sync.consumer.entity;

import lombok.Data;

import java.util.List;

/**
 * Created by zheng.yan on 2022/1/27.
 */
@Data
public class DeviceStatusReq implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备状态:0在线，1离线
     */
    private Integer deviceStatus;

    /**
     * 通道信息
     */
    private List<ChannelStatusReq> channelInfos;

}
