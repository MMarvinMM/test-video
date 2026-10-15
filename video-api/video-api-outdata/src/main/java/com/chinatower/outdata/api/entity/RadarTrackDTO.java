package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询雷达轨迹数据入参实体类
 */
@Data
public class RadarTrackDTO implements Serializable {

    /**
     * 开始时间（时间戳，单位：毫秒）
     */
    private Long startTime;

    /**
     * 结束时间（时间戳，单位：毫秒）
     */
    private Long endTime;

    /**
     * area是polygon时传参（84坐标系）,圆形区域传递64边型经纬度
     */
    private String area;

    /**
     * 目标ID
     */
    private String targetId;

    /**
     * 关键值
     */
    private String keyword;

    /**
     * 九位码mmsi号
     */
    private String mmsi;

    /**
     * 开始时间 2023-10-01 10:00:41
     */
    private String startDate;

    /**
     * 开始时间 2023-11-01 10:00:41
     */
    private String endDate;

    /**
     * 地图缩放等级
     */
    private String zoom;

    /**
     * 目标id
     */
    private String id;

    /**
     * 接入节点
     */
    private String accessNode;
}
