package com.chinatower.airline.dto.response;

import lombok.Data;

import java.util.List;

/**
 * @author csj
 * @description
 * @date 2024/11/4 19:58
 */
@Data
public class AirDeviceInfoCameraRep {

    /**
     * 相机参数ID
     * */
    private String cameraConfigId;
    /**
     * 相机型号
     * */
    private String cameraType;
    /**
     * 生产厂家
     * */
    private String cameraFactory;
    /**
     * Dji负载枚举
     * */
    private String djiDomain;
    /**
     * Dji负载枚举
     * */
    private String djiType;

    /**
     * Dji负载枚举
     * */
    private String djiSubType;

    /**
     * 云台俯仰角范围最大值
     * */
    private Double gimbalPitchMax;

    /**
     * 云台俯仰角范围最小值
     * */
    private Double gimbalPitchMin;
    /**
     * 云台偏航角范围最大值
     * */
    private Double gimbalYawMax;
    /**
     * 云台偏航角范围最小值
     * */
    private Double gimbalYawMin;
    /**
     * 是否支持激光测距 0：不支持 1：支持
     * */
    private Integer laserMeasureStatus;

    /**
     * 镜头列表
     * */
    private List<AirDeviceInfoCameraLensRep> cameraLensConfigList;

}
