package com.chinatower.common.core.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: hued
 * @Date: 2021年11月3日11:24:33
 * @Description:
 * 告警接收请求实体
 */
@Data
public class BaseAlarmVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 告警类型 */
    private String alarmType;
    /**
     * 告警时间
     * @required true
     */
    private String alarmTime;
    /**
     * 事件标题
     * @required true
     */
    private String alarmBody;

    /**
     * 设备编码（一键告警额外需要传设备编码）
     * @required true
     */
    private String deviceCode;

    /**
     * 设备名称（一键告警额外需要传设备名称）
     * @required true
     */
    private String deviceName;

    /**
     * 通道编码（一键告警额外需要传通道编码）
     * @required true
     */
    private String channelCode;

    /**
     * 通道名称（一键告警额外需要传通道名称）
     * @required true
     */
    private String channelName;
    /** P位置 */
    private String pan;
    /** T位置 */
    private String tilt;
    /** Z位置 */
    private String zoom;

    /**
     * 设备经度
     * @required true
     */
    private String longitude;

    /**
     * 设备纬度
     * @required true
     */
    private String latitude;
    /** 告警编码 */
    private String alarmCode;
    /** 算法编码 */
    private String algorithmCode;
    /** 原始图片文件url */
    private String originalPictureUrl;
    /** 监控任务唯一标识 */
    private String taskId;
    /** 报警视频url */
    private String alarmVideoUrl;
    /** 报警框图url */
    private String alarmPictureUrl;
    /** 告警等级 */
    private String importanceLevel;
}
