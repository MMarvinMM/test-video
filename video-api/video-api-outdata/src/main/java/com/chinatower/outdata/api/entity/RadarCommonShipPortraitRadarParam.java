package com.chinatower.outdata.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 雷达船舶画像返回实体类
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RadarCommonShipPortraitRadarParam implements Serializable {

    /**
     * 船舶唯一标识 or 目标ID
     */
    private String id;

    /**
     * 持续时长
     */
    private String duration;

    /**
     * 船长
     */
    private String len;

    /**
     * 最大船长
     */
    private String maxLen;

    /**
     * 最大速度
     */
    private String maxSpeed;

    /**
     * 平均速度
     */
    private String highSpeedAvg;

    /**
     * 单拖开始时间
     */
    private String singStartTime;

    /**
     * 单拖作业时长：秒
     */
    private String singleDuration;

    /**
     * 围网开始时间
     */
    private String seineStartTime;

    /**
     * 围网作业时长：秒
     */
    private String seineDuration;

    /**
     * 行为名称
     */
    private String behaviorName;

    /**
     * 搭靠时间
     */
    private String alarmStartDateTime;

    /**
     * MMSI
     */
    private String mmsi;

    /**
     * 船名
     */
    private String vesselName;

    /**
     * 最近距离
     */
    private String approachMinDistanceCal;

    /**
     * 消失时的距离
     */
    private String disappearPointDistance;

    /**
     * 高速持续时长ms
     */
    private String highSpeedDuration;

    /**
     * 高速平均速度
     */
    private String avgHighSpeed;

    /**
     * 低速时长ms
     */
    private String lowSpeedDuration;

    /**
     * 低速平均
     */
    private String avgLowSpeed;

    /**
     * 航速差
     */
    private String speedDifference;
}
