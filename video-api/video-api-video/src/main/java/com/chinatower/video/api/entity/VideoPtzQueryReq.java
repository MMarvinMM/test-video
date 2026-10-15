package com.chinatower.video.api.entity;

import lombok.Data;

@Data
public class VideoPtzQueryReq {

    /**
     * 摄像机经度
     * @required true
     */
    private double cameraLon;

    /**
     *  摄像机纬度
     *  @required true
     */
    private double cameraLat;

    /**
     *  打点经度
     *  @required true
     */
    private double pointLon;

    /**
     *  打点纬度
     *  @required true
     */
    private double pointLat;

    /**
     *  设备编码
     *  @required true
     */
    private String deviceCode;

    /**
     *  通道编码
     *  @required true
     */
    private String channelCode;

    /**
     *  租户id
     */
    private String tenantId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 飞行高度，单位：米
     *  转动无人机时，必填
     *  @required true
     */
    private String flyHeight;
}
