package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 告警模板相关信息
 */
@Data
public class TemplateRelInfo implements Serializable {

    /**
     * 目标类型
     */
    private List<String> targetType;

    /**
     * 船舶类型
     */
    private List<String> shipType;

    /**
     *最小航速
     */
    private String minSpeed;

    /**
     * 最大航速
     */
    private String maxSpeed;

    /**
     * 最小持续时长
     */
    private String keepTime;

    /**
     * 最大船长
     */
    private String maxShipLength;

    /**
     * 最小船长
     */
    private String minShipLength;

    /**
     * 最小移动范围
     */
    private String minMoveRange;

    /**
     * 开始航向
     */
    private String startCourse;

    /**
     * 结束航向
     */
    private String endCourse;

    /**
     * 目标状态
     */
    private List<String> targetStatus;

    /**
     * 船籍
     */
    private List<String> shipFrom;

    /**
     * 过线方向
     */
    private String crossDir;

    /**
     * 最小滞留时长
     */
    private String minStopTime;

    /**
     * 最大滞留范围
     */
    private String maxStopRange;

    /**
     * 最小滞留范围
     */
    private String minStopRange;

    /**
     * 异常轨迹
     */
    private String errorTrack;

    /**
     * 两船最小距离
     */
    private String minDistance;

    /**
     * 两船最大距离
     */
    private String maxDistance;

    /**
     * 最小轨迹并行值
     */
    private String minParallelValue;

    /**
     * 最大轨迹并行值
     */
    private String maxParallelValue;

    /**
     * 最小会遇距离
     */
    private String minMeetDistance;

    /**
     * 最小会遇时间
     */
    private String meetTime;

    /**
     * 最小关闭时间
     */
    private String closeTime;

    /**
     * 光雷联动信息
     */
    private TargetFollowInfo targetFollowInfo;

}
