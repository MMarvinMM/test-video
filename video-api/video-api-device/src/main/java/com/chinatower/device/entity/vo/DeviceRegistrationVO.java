package com.chinatower.device.entity.vo;

import lombok.Data;

@Data
public class DeviceRegistrationVO {

    /**
     * 摄像机编码
     */
    private String deviceCode;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 三维模型Url
     */
    private String url;

    /**
     * 配准开关 0：关闭 1：开启
     */
    private String registrationSwitch;

    /**
     * 配准状态 0：未配准 1：已配准
     */
    private String registrationStatus;

    /**
     * 配准类型 1：维持配准  2：预置位配准
     */
    private String registrationType;

    /**
     * 预置位编码
     */
    private Long presetId;

    /**
     * 坐标系信息
     */
    private CoordinateSystem coordinateSystem;

    /**
     * 预置位配准信息
     */
    private VideoDirectionalInfo registrationInfo;

    private VideoDirectionalInfoSmall registrationInfoSmall;

    /**
     * 预置位配准信息JSON串
     */
    private String registrationInfoStr;

    private String extendJson;

    private String tenantId;

    private String industryCode;

}
