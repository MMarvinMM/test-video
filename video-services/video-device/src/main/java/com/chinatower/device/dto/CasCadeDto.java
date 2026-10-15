package com.chinatower.device.dto;

import lombok.Data;

/**
 * 级联平台信息
 */
@Data
public class CasCadeDto {

    /**
     * 租户id
     */
    private String tenantId;

    /**
     *级联平台编码
     */
    private String platCode;

    /**
     * 摄像机编码
     */
    private String deviceCode;

    /**
     * 设备名称
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
     * 自定义通道编码
     */
    private String customChannelCode;

    /**
     * 组织编码
     */
    private String orgCode;

    /**
     * 摄像机操控权限类型(逗号分割)：1-录像操作；2-云台操作；3-看视频
     */
    private String permissionType;


    /**
     *级联开启状态
     */
    private String isCascade;
}
