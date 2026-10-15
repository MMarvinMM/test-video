package com.chinatower.device.entity.vo;

import lombok.Data;
import lombok.ToString;

import java.util.Set;

/**
 * 地图展示渠道信息vo迁移
 * @return:
 * @author: zheng.yan
 * @date: 2022/3/11
 * @version:
 */
@Data
@ToString
public class ChannelInfoScrVO {
    private String status;
    private Integer streamType;
    private Integer maxGuard;
    private Integer maxPreset;
    private Integer maxPatrol;
    /**
     * 通道编码
     */
    private String channelCode;
    /**
     * 通道名称
     */
    private String channelName;
    /**
     * 设备编码
     */
    private String deviceCode;
    /**
     * 外型 1-球机；2-半球；3-枪机
     */
    private String facade;
    /**
     * 通道类型 1-数字信号；2-模拟信号；3-音频信号；4-i/o输入；5-i/o输出；
     */
    private String channelType;
    /**
     * 可视范围
     */
    private String visualRange;
    /**
     * 水平辐射角度
     */
    private String horizRange;
    /**
     * 俯仰辐射角度
     */
    private String vertiRange;

    private Set<String> channelCodeSet;

    private String tenantId;

    private String isMonitor;

    private String longitude;

    private String latitude;

    private String location;

    private String height;

    private String siteCode;

    private String modelCode;

    private String modelName;

    private int rangeMeter;

    private String distance;

    private Integer channelStatus;
    private String parentDeviceCode;

    private String imgUrl;
}
