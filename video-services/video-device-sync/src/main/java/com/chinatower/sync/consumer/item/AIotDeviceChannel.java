package com.chinatower.sync.consumer.item;

import lombok.Data;

import java.io.Serializable;


/**
 * 4.8.设备/通道同步-到其他系统-队列推送 请求实体类
 */
@Data
public class AIotDeviceChannel implements Serializable{
    /**
     * 通道状态，0离线，1在线，2未配置设备
     */
    private Integer channelStatus;

    /**
     * 通道编码，国标编码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 外型，1-球机；2-半球；3-枪机
     */
    private Integer facade;

    /**
     * 通道类型，1-数字信号；2-模拟信号；3-音频信号；4-i/o输入；5-i/o输出；
     */
    private Integer channelType;

    /**
     * 码流类型，1-主码流；2-子码流
     */
    private Integer streamType;

    /**
     * 可设置最大预置点数量
     */
    private Integer maxPreset;

    /**
     * 可设置最大巡航路径数量
     */
    private Integer maxPatrol;

    /**
     * 可视范围，单位：公里
     */
    private String visualRange;

    /**
     * 水平辐射角度，取值范围0到360，例如：090；10270；等
     */
    private String horizRange;

    /**
     * 俯仰辐射角度，取值范围-90到90，例如：-9090；-7060；等
     */
    private String vertiRange;

    /**
     * 删除标志（0代表存在 2代表删除），0正常 其他是逻辑删除，需要给下游判断是否是删除
     */
    private String delFlag;

}

