package com.chinatower.device.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 无人机指点飞行卡片VO
 */
@Data
public class UavPointToFlyCardVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 无人机名称
     */
    @ApiModelProperty(value = "无人机名称")
    private String uavName;

    /**
     * 设备编码
     */
    @ApiModelProperty(value = "设备编码")
    private String deviceCode;

    /**
     * 电量（百分比）
     */
    @ApiModelProperty(value = "电量（百分比）")
    private String batteryPercent;

    /**
     * 飞行状态（0-待执行、1-飞行中、2-空闲）
     */
    @ApiModelProperty(value = "飞行状态（0-待执行、1-飞行中、2-空闲）")
    private String flightStatus;

    /**
     * 距离（单位：千米，保留1位小数）
     */
    @ApiModelProperty(value = "距离（单位：千米，保留1位小数）")
    private String distance;

    /**
     * 预计飞行时间（单位：分钟）
     */
    @ApiModelProperty(value = "预计飞行时间（单位：分钟）")
    private Integer estimatedFlightTime;

    /**
     * 相机信息列表
     */
    @ApiModelProperty(value = "相机信息列表")
    private List<DeviceCameraModelVO> cameraList;

    /**
     * 续航信息（预计X公里 X分钟）
     */
    @ApiModelProperty(value = "续航信息")
    private String endurance;

    /**
     * 挂载设备
     */
    @ApiModelProperty(value = "挂载设备")
    private UavMountModelInfoVO uavMountModel;

    /**
     * 距计划起飞还剩（格式：距A起飞还剩：X分钟，A为计划名称，X为时长）
     */
    /**
     * 计划时间
     */
    private String planTime;
    /**
     * 计划名称
     */
    private String planName;

    /**
     * 是否推荐
     */
    @ApiModelProperty(value = "是否推荐")
    private Boolean recommended;

    /**
     * 机巢编码
     */
    @ApiModelProperty(value = "机巢编码")
    private String uavNestCode;

    /**
     * 机巢名称
     */
    @ApiModelProperty(value = "机巢名称")
    private String uavNestName;

    /**
     * 机巢经度
     */
    @ApiModelProperty(value = "机巢经度")
    private String uavNestLongitude;

    /**
     * 机巢纬度
     */
    @ApiModelProperty(value = "机巢纬度")
    private String uavNestLatitude;

    /**
     * 机巢高度
     */
    @ApiModelProperty(value = "机巢高度")
    private String uavNestAltitude;

    /**
     * 无人机经度
     */
    @ApiModelProperty(value = "无人机经度")
    private String longitude;

    /**
     * 无人机纬度
     */
    @ApiModelProperty(value = "无人机纬度")
    private String latitude;

    /**
     * 接入节点
     */
    @ApiModelProperty(value = "接入节点")
    private String accessNode;

}
