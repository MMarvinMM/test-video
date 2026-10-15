package com.chinatower.message.station.entity;

import lombok.Data;

@Data
public class UavEventHms {

    /**
     * HMS 编码
     */
    private String hmsCode;
    /**
     * HMS 消息
     */
    private String hmsMsg;
    /**
     * 告警等级{"0":"通知","1":"提醒","2":"警告"}
     */
    private String hmsLevel;
    /**
     * 事件模块{"0":"飞行任务","1":"设备管理","2":"媒体","3":"hms"}
     */
    private String module;
    /**
     * 设备编码
     */
    private String deviceCode;
    /**
     * 是否飞行{"0":"在地上","1":"在天上"}
     */
    private String inTheSky;
    /**
     * 设备类型
     * 格式为 {domain-type-subtype}
     * 通过 domain、type、sub_type 可以唯一地确定一款设备（可以为飞行器、负载、遥控器等）
     * domain 表示一个领域，作为一个命名空间，暂时分为
     * 0，飞机类
     * 1，负载类
     * 2，遥控器类
     * 3，机场类
     */
    private String deviceType;
    /**
     * 是否为及时性的
     * {"0":"否","1":"是"}代表是否为及时性的告警码。比如风过大，会随着风力减小而自动消失。
     */
    private String imminent;
}
