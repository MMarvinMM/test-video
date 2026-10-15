package com.chinatower.device.entity.dto;

import lombok.Data;

/**
 * 卫星信息实体类
 * 包含RTK和GPS搜星数量、状态等相关信息
 */
@Data
public class SatelliteInfo {

    /**
     * RTK搜星数量
     */
    private String pitch;

    /**
     * GPS搜星数量
     */
    private String roll;

    /**
     * RTK状态（只支持自研）
     * 可选值：NONE(无)、SINGLE_POINT(单点解)、FLOAT(浮点解)、FIXED_POINT(固定解)、UNKNOWN(未知)
     */
    private String rtkKey;

    /**
     * 是否收敛（只支持大疆无人机）
     * 0:非开始, 1:收敛中, 2:收敛成功, 3:收敛失败
     */
    private Integer isFixed;

    /**
     * 搜星档位（只支持大疆无人机）
     * 0:0档, 1:1档, 2:2档, 3:3档, 4:4档, 5:5档, 10:RTK 正在使用中
     */
    private Integer quality;

    /**
     * RTK可用状态（只做展示）
     */
    private Integer rtkReady;

    /**
     * RTK开关状态（只做展示）
     */
    private Integer rtkEnable;

    /**
     * RTK网络状态（只做展示）
     * 可选值：TRANSMITTING(传输中)、CONNECTING(连接中)、DISCONNECTED(已断连)
     * 空值表示未知
     */
    private String rtkNetwork;

    private String deviceCode;

    private String industryCode;

    private WebSocketMsgType webSocketMsgType = WebSocketMsgType.UFCP_UAV_SATELLITE_INFO;
}
