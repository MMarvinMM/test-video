package com.chinatower.platform.consumer.item;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lzt
 */
@Data
public class DeviceIndexInfoVO implements Serializable {

    private String id;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 上报时间
     */
    private String reportTime;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 水位
     */
    private String waterLevelIndex;

    /**
     * 水质
     */
    private String waterQualityIndex;

    private String msgid;
}
