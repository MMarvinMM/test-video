package com.chinatower.event.api.model;

import com.chinatower.event.entity.vo.UavNestDeviceVo;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @program: DeviceInfoEntity
 * @description: 设备信息
 * @author: lyy
 * @create: 2023/11/01 14:00
 */
@Data
@ToString
public class DeviceInfoEntity {

    private String tenantId;

    private String industryCode;

    /**
     * 设备编号
     */
    private String deviceCode;

    private String vidiconCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 摄像机标签
     * */
    private List<String> deviceLabel;

    /**
     * 摄像机类型
     */
    private String deviceType;
    /**
     * 摄像机类型名称
     */
    private String deviceTypeName;

    /**
     * 摄像机子类型
     */
    private String deviceSubType;
    /**
     * 摄像机子类型名称
     */
    private String deviceSubTypeName;


    private String status;

    private String longitude;

    private String latitude;

    private String location;

    private String height;

    /**
     * 站址编码
     */
    private String siteCode;

    private String provinceId;
    private String cityId;
    private String countyId;
    private String provinceName;
    private String cityName;
    private String countyName;

    private String modelCode;
    private String modelId;

    /**
     * 物联设备型号编码
     */
    private String deviceModelCode;

    /**
     * 设备收藏标记
     */
    private String deviceCodeCollect;

    /**
     * 摄像机焦距范围
     */
    private String focusRange;

    /**
     * 水平向下为负 (0-否 1-是)
     */
    private String vertiDownNeg;

    /**
     * 告警定位（1:开启 0关闭）
     */
    private String alarmLocation;

    /**
     * 雷达设备厂家名称
     */
    private String modelName;

    /**
     * 摄像机厂家名称
     */
    private String modelCodeName;

    /**
     * 物联设备厂家名称
     */
    private String modelFactoryName;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 抓图时间
     */
    private String snapShotTime;

    /**
     * 无人机机巢信息
     */
    private List<UavNestDeviceVo> uavNestDeviceList;
}
