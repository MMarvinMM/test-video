package com.chinatower.event.vo.entity;

import lombok.Data;

/**
 * @className: ChannelSuppressVo
 * @description: 通道对象
 * @author: li.xufu
 * @date: 2022/5/16
 * @requestID 需求单号及需求名称
 */
@Data
public class ChannelSuppressVO {
    private String deviceCode; //设备编码
    private String channelName; //通道名称
    private String channelCode; //通道编码
    private String orderCode; //订单编码
    private String tenantId; //客户编码
    /**
     * p值 设备
     */
    private String pointsP;
    /**
     * t值 设备
     */
    private String pointsT;
    /**
     * z值 设备
     */
    private String pointsZ;

    /**
     * 标识：选择0-摄像机名称/1-通道名称
     */
    private String nameType;
}
