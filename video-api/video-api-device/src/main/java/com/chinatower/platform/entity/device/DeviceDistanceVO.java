package com.chinatower.platform.entity.device;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceDistanceVO implements Serializable {

    /**
     * 设备编码
     * @required true
     */
    private String deviceCode;

    /**
     * 名称
     * @required true
     */
    private String name;

    /**
     * 位置
     * @required true
     */
    private String location;

    /**
     * 距离
     * @required true
     */
    private String distance;

    /**
     * 经度
     * @required true
     */
    private String longitude;

    /**
     * 纬度
     * @required true
     */
    private String latitude;

    /**
     * 海拔
     */
    private String altitude;

    /**
     * 设备状态
     */
    private String deviceStatus;

    /**
     * 设备挂高，单位：米
     */
    private String height;

    /**
     * 雷达覆盖范围 单位：米
     */
    private String wZone;

    /**
     * 厂家编码
     */
    private String modelCode;

    /**
     * 厂家名称
     */
    private String modelName;

    /**
     * 设备类型编码
     */
    private String deviceType;

    /**
     * 设备类型名称
     */
    private String deviceTypeName;

    /**
     * 是否收藏 0未收藏 1已收藏
     */
    private Integer isMonitor;

    /**
     * 设备原始状态
     */
    private String deviceOriginalStatus;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 无人机机巢编码
     */
    private String uavNestCode;

    /**
     * 无人机机巢名称
     */
    private String uavNestName;

    /**
     * 无人机机巢状态
     */
    private String uavNestStatus;

    /**
     * 无人机飞行状态
     */
    private String flyStatus;

    /**
     * 设备型号
     */
    private String deviceModelCode;
}
