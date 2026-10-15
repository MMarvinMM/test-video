package com.chinatower.airline.dto.request;

import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2025/1/16 9:34
 */
@Data
public class AirPointToFlyReq {
    /**
     * 设备编码
     */
    private String deviceCode;
    /**
     * 机巢编码
     */
    private String uavNestCode;
    /**
     * 飞控返回报文编码
     */
    private String returnMode;
    /**
     * 经度
     */
    private String longitude;
    /**
     * 纬度
     */
    private String latitude;
    /**
     * 高度
     */
    private String altitude;
    /**
     * 受理节点
     */
    private String acceptNode;
    /**
     * 接入节点
     */
    private String accessNode;
    /**
     * 航线id
     */
    private String flightRouteId;
    /**
     * 设置的高度
     */
    private String height;
    /**
     * 新指点飞行kmzJson
     */
    private String kmzJson;
    /**
     * 指点飞行算法配置信息
     */
    private String algorithmCodes;
    /**
     * kmz文件名称
     */
    private String fileName;
    /**
     * kmz文件可访问路径
     */
    private String kmzAccessUrl;

    /**
     * 喇叭配置
     */
    private String hornInfo;
    /**
     * 航线文件uos信息 解析需要使用
     */
    private String uosInfo;
}
