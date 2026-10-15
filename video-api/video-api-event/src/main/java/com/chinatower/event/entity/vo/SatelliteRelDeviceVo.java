package com.chinatower.event.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @className: IotRelDeviceVo
 * @description: 物联设备关联的摄像机信息
 * @author: yongqi.han
 * @date: 2022/7/19
 * @requestID 需求单号及需求名称
 */
@Data
public class SatelliteRelDeviceVo implements Serializable {
    /**
     * 摄像机编号
     */
    private String deviceCode;
    /**
     * 摄像机名称
     */
    private String deviceName;

    /**
     * 摄像机标签
     * */
    private List<String> deviceLabel;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 摄像机地址
     */
    private String deviceAddress;

    /**
     * 摄像机厂家
     */
    private String modelCode;

    /**
     * 摄像机地址 与TenantIotConfRelVO做映射
     */
    private String location;

    /**
     * 摄像机厂家名称
     */
    private String modelCodeName;

    /**
     * 摄像机高度
     */
    private String height;

    /**
     * 抓图时间
     */
    private String snapShotTime;

    /**
     * 通道名称
     */
    private String realChannelName;

    /**
     * 设备收藏标记
     */
    private String deviceCodeCollect;

    /**
     * 摄像机图标地址
     * */
    private String deviceImageUrl;

    /**
     * 摄像机类型
     */
    private String deviceType;

    /**
     * 摄像机类型名称
     */
    private String deviceTypeName;
}
