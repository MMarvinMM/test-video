package com.chinatower.device.entity.vo;

import io.swagger.models.auth.In;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 地图展示设备渠道信息vo迁移DeviceChannelChVO
 * @return:
 * @author: zheng.yan
 * @date: 2022/3/11
 * @version:
 */
@Data
@ToString
public class DeviceChannelInfoVO {
    /**
     * 站址编码
     */
    private String siteCode;

    /**
     * 站址名称
     */
    private String siteName;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    private String devName;

    private String deviceCode;

    private String status;

    private String height;

    private String location;

    private List<ChannelInfoScrVO> channels;

    private String orderCode;

    private String tenantId;

    // 是否收藏 0未收藏 1已收藏
    private String isMonitor;
    // ar标签是否收藏 0未收藏 1已收藏
    private String tagCollect;

    private String modelCode;

    private String modelName;

    /**
     * 设备类型:1-IPC 0-NVR;2-AR摄像头；3-热成像摄像头
     */
    private String categoryCode;

    private String deviceModelCode;

    private List<String> labelNameList;

    private String visualRadius;

    private String alarmLocation;

    /**
     * 设备所属组织编码
     */
    private String orgCode;
    /**
     * 设备所属组织名称
     */
    private String orgName;

    /**
     * 设备所属组织主键
     */
    private String orgRelId;

    /**
     * 附件地址列表
     */
    private List<String> fileUrlList;

    private String deviceName;
    private Integer deviceStatus;
    private String provinceCode;
    private String provinceName;
    private String cityCode;
    private String cityName;
    private String countyCode;
    private String countyName;
    private String streetCode;
    private String streetName;
    private String admCode;
    private String industryCode;
    private String deviceStatusName;
    private String orderStatus;
    private String orderStatusName;
    private String modelId;
    private String collectType;
    private String collectMethod;
    private String channelCode;
    private String channelName;
    /**
     *  是否有未完结告警（0-否、1-是）
     */
    private String isCompleteFlag;

    /**
     * 配准开关 0：关闭 1：开启
     */
    private String registrationSwitch;

    /**
     * 摄像机配准信息
     */
    private DeviceRegistrationVO deviceRegistration;
}
