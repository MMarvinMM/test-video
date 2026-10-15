package com.chinatower.industry.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 行业应用设备信息
 * Created by zheng.yan on 2022/3/22.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DeviceIndDTO implements java.io.Serializable {
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
     * 设备状态:0在线，1离线
     */
    private Integer deviceStatus;
    /**
     * 省份
     */
    private String provinceCode;
    /**
     * 省份名称
     */
    private String provinceName;
    /**
     * 地市
     */
    private String cityCode;
    /**
     * 地市名称
     */
    private String cityName;

    /**
     * 区县
     */
    private String countyCode;
    /**
     * 区县名称
     */
    private String countyName;

    /**
     * 乡镇/街道
     */
    private String streetCode;
    /**
     * 乡镇/街道
     */
    private String streetName;

    /**
     * 行政区域
     */
    private String admCode;
    /**
     * 设备类型:1-IPC 0-NVR;2-AR摄像头；3-热成像摄像头
     */
    private Integer categoryCode;
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
     * 设备挂高，单位：米
     */
    private String height;
    /**
     * 通道
     */
    private List<ChannelIndDTO> channels;
    /**
     * 设备厂家
     */
    private String modelCode;
    /**
     * 通道编码
     */
    private String channelCode;
    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 接入协议
     */
    private String accessProtocols;

    /**
     * 接入节点
     */
    private String accessNode;
}
