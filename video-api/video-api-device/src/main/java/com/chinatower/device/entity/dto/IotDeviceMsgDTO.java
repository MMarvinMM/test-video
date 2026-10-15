package com.chinatower.device.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 物联设备表
 * </p>
 *
 * @author AutoGen
 * @since 2022-05-07
 */
@Data
public class IotDeviceMsgDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 设备状态:0在线，1离线
     */
    @JsonProperty("status")
    private Integer deviceStatus;

    /**
     * 设备类型，运管录入，各系统与运管同步
     */
    private String deviceType;

    /**
     * 省份
     */
    private String provinceId;

    /**
     * 地市
     */
    private String cityId;

    /**
     * 区县
     */
    private String countyId;

    /**
     * 设备类型 1-岸基雷达；2-传感器；
     */
    private Integer categoryCode;

    /**
     * 业务系统站址编码
     */
    private String siteCode;

    /**
     * 业务系统站址名称
     */
    private String siteName;

    /**
     * 公共库站址编码
     */
    private String publicSiteCode;

    /**
     * 设备挂高，单位：米
     */
    private String height;

    /**
     * 站址地址
     */
    private String location;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 所属管理区域
     */
    private String areaCode;

    /**
     * 行政区域
     */
    private String admCode;

}
