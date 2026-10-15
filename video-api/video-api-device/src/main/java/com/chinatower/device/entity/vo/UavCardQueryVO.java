package com.chinatower.device.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 无人机卡片列表返回对象
 */
@Data
public class UavCardQueryVO implements Serializable {

    /**
     * 设备编码
     */
    private String code;

    /**
     * 设备名称
     */
    private String name;
    private String deviceName;

    /**
     * 设备别名
     */
    private String deviceAlias;

    /**
     * 设备状态（0在线，1离线）
     */
    private String status;

    /**
     * 机巢编码
     */
    private String uavNestCode;

    /**
     * 机巢名称
     */
    private String uavNestName;

    /**
     * 机巢状态
     */
    private String uavNestStatus;

    /**
     * 机巢经度
     */
    private String uavNestLongitude;

    /**
     * 机巢纬度
     */
    private String uavNestLatitude;

    /**
     * 机巢标定状态
     */
    private Integer uavNestCalibrationStatus;

    /**
     * 机巢是否位置固定 1-固定 0-不固定
     */
    private Integer nestPositionFix;


    /**
     * 乡镇编码
     */
    private String townCode;

    /**
     * 乡镇名称
     */
    private String townName;

    /**
     * 节点层级
     */
    private String level;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 设备数量
     * @required true
     */
    private Integer num = 0;

    /**
     * 在线数量
     * onlineNum
     * @required true
     */
    private Integer onlineNum = 0;

    private Integer videoNum = 0;

    /**
     * 飞行状态（0-待执行、1-飞行中、2-空闲）
     */
    private String flyStatus;

    /**
     * 执行时间（HH:MM）
     */
    private String planTime;

    /**
     * 是否收藏
     * 0未收藏 1已收藏
     */
    private String isMonitor = "0";

    /**
     * 标签是否收藏
     * 0未收藏 1已收藏
     */
    private String tagCollect = "0";

    /**
     * 收藏数量
     */
    private int isMonitorNum = 0;

    /**
     * 收藏数量
     */
    private int isTagCollectNum = 0;

    /**
     * 站址编码
     */
    private String siteCode;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 海拔
     */
    private String altitude;

    private int channelStatusCount;

    /**
     *  有告警/无告警状态 （0无告警、1有告警）
     */
    private String isExsistAlarm;

    /**
     * 航线ID
     */
    private String flightRouteId;

    /**
     * 执行状态（0待执行 1执行成功 2执行失败 3执行中）
     */
    private String taskStatus;

    /**
     * 挂载型号编码  新版本1.8.30后 多挂载 用,拼接
     */
    private String uavMountModel;

    /**
     * 机巢是否固定 1-固定 0-不固定
     */
    private Integer uavNestPositionFix;

    /**
     * true-机巢  false-无人机
     */
    @JsonProperty("isNestDevice")
    private boolean isNestDevice;
}
