package com.chinatower.device.dto;

import com.chinatower.device.entity.vo.RadarAlarmBlackWhiteVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 雷达告警同步信息实体类
 */
@Data
public class AlarmTemplateInfo implements Serializable {

    /**
     * 报警名称
     */
    private String alarmName;

    /**
     * 报警模板编号
     */
    private int alarmCode;

    /**
     * 开始报警日期（以天为单位，例如2021-04-16）。默认长期
     */
    private String startDate;

    /**
     * 结束报警日期（以天为单位，例如2021-04-18）。默认长期
     */
    private String endDate;

    /**
     * 开始时间，以分钟为单位，例如20:34。默认长期
     */
    private String startTime;

    /**
     * 结束时间以分钟为单位，例如20:34。默认长期
     */
    private String endTime;

    /**
     * 船舶类型集合
     */
    private List<String> shipType;

    /**
     * 目标类型集合
     */
    private List<String> targetType;

    /**
     * 目标状态。1代表正常状态；2代表预测状态。传哪个显示哪个状态的。
     */
    private List<String> targetStateList;

    /**
     * 1表示中国，2表示中国港澳台，0表示外国
     */
    private List<String> nationalityList;

    /**
     * 光电跟踪录像时间（单位：分钟）。不传表示不跟踪录像，默认不跟踪录像
     */
    private int cameraFollow;

    /**
     * 自动抓拍（单位：张）。不传表示不抓拍，默认不抓拍
     */
    private int autoPhoto;

    /**
     * 速度最大值（节）
     */
    private String speedMax;

    /**
     * 速度最小值（节）
     */
    private String speedMin;

    /**
     * 最大航向(单位：度)
     */
    private String maxHeading;

    /**
     * 最小航向(单位：度)
     */
    private String minHeading;

    /**
     * 最小船长（米）
     */
    private String minLen;

    /**
     * 最大船长（米）
     */
    private String maxLen;

    /**
     * 目标最小移动范围（米）
     */
    private String minMovingDistance;

    /**
     * 目标最小持续时长（秒）
     */
    private String minMovingDuration;

    /**
     * 最大滞留范围（米）
     */
    private String moveRangeMax;

    /**
     * 最小滞留范围（米）
     */
    private String moveRangeMin;

    /**
     * 最小滞留时长（秒）
     */
    private String timePeriod;

    /**
     * 黑名单列表，list内数据结构：[{"shipName":"yu982","mmsi":"413339760"},{"shipName":"yu982","mmsi":"413339760"}]
     */
    private List<RadarAlarmBlackWhiteVO> blackWhiteListList;

    /**
     * 最小关闭时间（单位：分钟）
     */
    private String closeTime;

    /**
     * 会遇距离（米）
     */
    private String dcpa;

    /**
     * 会遇时间（分）
     */
    private String tcpa;

    /**
     * 轨迹并行最大值（度）
     */
    private String targetAngleMax;

    /**
     * 轨迹并行最小值（度）
     */
    private String targetAngleMin;

    /**
     * 最大两船距离（米）
     */
    private String targetDistanceMax;

    /**
     * 最小两船距离（米）
     */
    private String targetDistanceMin;

    /**
     * 白名单列表，list内数据结构：[{"shipName":"yu982","mmsi":"413339760"},{"shipName":"yu982","mmsi":"413339760"}]
     */
    private List<RadarAlarmBlackWhiteVO> alarmWhiteLists;

    /**
     * 过线方向
     */
    private String lineTest;

    /**
     * 受理节点
     */
    private String acceptNode;

    /**
     * 接入节点
     */
    private String accessNode;
}
