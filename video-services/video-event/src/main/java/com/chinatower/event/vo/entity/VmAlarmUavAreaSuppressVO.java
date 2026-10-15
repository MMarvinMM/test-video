package com.chinatower.event.vo.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Author: songxinfeng
 * @Date: 2025/9/17 17:19
 */

@Data
public class VmAlarmUavAreaSuppressVO {

    /**
     * area_id
     */
    private Integer id;
    /**
     * 告警压制策略ID
     */
    private Integer alarmSuppressId;

    /**
     * 无人机设备编码
     */
    private String uavDeviceCode;

    /**
     * 无人机设备名称
     */
    private String uavDeviceName;

    /**
     * 航线id
     */
    private String flightRouteId;

    /**
     * 航线名称
     */
    private String flightRouteName;

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 图层id
     */
    private String layerId;

    /**
     * 经纬度点位信息
     */
    private String point;

    /**
     * 点位json数据
     */
    private String pointJson;

    /**
     * 租户
     */
    private String tenantId;

    /**
     * 行业
     */
    private String industryCode;

    /**
     * 删除标识
     */
    private String deleteFlag;

    private Date createTime;

    private Date updateTime;

    private String createUser;

    private String updateUser;
}
