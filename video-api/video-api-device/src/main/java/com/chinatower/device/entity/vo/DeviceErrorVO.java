package com.chinatower.device.entity.vo;

import lombok.Data;
import lombok.ToString;

/**
 * 设备故障消息表
 */
@Data
@ToString
public class DeviceErrorVO {

    private String errorId;

    /**
     * 设备编码
     * @required true
     */
    private String deviceCode;

    /**
     * 设备名称
     * @required true
     */
    private String deviceName;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 告警类型
     * @required true
     */
    private String alarmType;

    /**
     * 告警时间
     * @required true
     */
    private String alarmTime;

    /**
     * 告警内容
     * @required true
     */
    private String alarmBody;

    private String createTime;

    /**
     * 告警编码
     * @required true
     */
    private String alarmCode;
}
