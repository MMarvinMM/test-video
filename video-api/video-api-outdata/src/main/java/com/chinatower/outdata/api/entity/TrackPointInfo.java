package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 轨迹信息
 */
@Data
public class TrackPointInfo implements Serializable {
    /**
     * 目标id
     */
    private String id;
    /**
     * 时间戳
     */
    private String lastTm;

    /**
     * 目标航速
     */
    private String speed;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 目标mmsi
     */
    private String mmsi;

    /**
     * 目标长度
     */
    private String len;

    /**
     * 航向
     */
    private String course;

    /**
     * 时间戳
     */
    private String lastDt;

    /**
     * 目标状态
     */
    private String staticState;

    /**
     * 北斗终端号
     */
    private String terminal;

    /**
     * 持续时长
     */
    private String durationMinutes;

    /**
     * 北斗终端号
     */
    private String sclass;

}
