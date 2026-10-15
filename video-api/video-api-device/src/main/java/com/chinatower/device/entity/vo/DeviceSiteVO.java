package com.chinatower.device.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName DeviceSiteVO
 * @Description
 * @Author chenshaowu
 * @Date 2022/3/11
 **/

@Data
public class DeviceSiteVO {

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备状态
     */
    private String status;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 设备地址
     */
    private String location;

    /**
     * 挂高
     */
    private String height;

    /**
     * 站址编码
     */
    private String siteCode;

    /**
     * 设备厂家编码
     */
    private String modelCode;

    /**
     * 设备厂家名称
     */
    private String modelName;

    /**
     * 设备厂家id
     */
    private String modelId;

    /**
     * 焦距范围
     */
    private String focusRange;

    /**
     * 水平向下为负  0否1是.
     */
    private String vertiDownNeg;
    /**
     * 设备类型:1-IPC 0-NVR;2-AR摄像头；3-热成像摄像头
     */
    private String categoryCode;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 设备厂家编码
     */
    private String deviceModelCode;

    /**
     * 省编码
     */
    private String provinceId;

    /**
     * 市编码
     */
    private String cityId;

    /**
     * 区编码
     */
    private String countyId;

    /**
     * 街道编码
     */
    private String townCode;

    /**
     * 设备状态
     */
    private String deviceStatus;

    /**
     * 是否查询收藏  0-收藏，1-所有
     */
    private String isMonitor;

    /**
     * 设备状态类型
     */
    private String deviceTypeName;

    /**
     * 雷达天线扫描周期 单位：秒
     */
    private String devicePeriod;

    /**
     * 雷达最大探测半径
     */
    private String wZone;

    /**
     * 相控角度
     */
    private String parAngle;

    /**
     * 附件地址列表
     */
    private List<String> fileUrlList;

    /**
     * 子节点列表
     */
    private List<DeviceSiteVO> list;

    /**
     * 订单编码
     */
    private String orderCode;

    /**
     * 厂家编码
     */
    private String manufacturerCode;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 厂家名称
     */
    private String manufacturerName;

    /**
     * 省编码
     */
    private String provinceCode;

    /**
     * 市编码
     */
    private String cityCode;

    /**
     * 区编码
     */
    private String countyCode;

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
     *  是否有未完结告警（0-否、1-是）
     */
    private String isCompleteFlag;

    /**
     * 设备父类型
     */
    private String deviceSubType;

    /**
     * 是否机巢设备
     */
    private Boolean isNestDevice;

    /**
     * 机巢设备类型
     */
    private String uavNestPositionFix;

    /**
     * 机巢标定状态
     */
    private Integer uavNestCalibrationStatus;
}