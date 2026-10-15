package com.chinatower.device.entity.dto;

import lombok.Data;

/**
 * Created by zheng.yan on 2022/1/21.
 */
@Data
public class BasicDeviceChannelBo implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 通道状态，0在线，1离线，2未配置设备
     */
    private Integer status;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 外型 1-球机；2-半球；3-枪机
     */
    private Integer facade;

    /**
     * 通道类型 1-数字信号；2-模拟信号；3-音频信号；4-i/o输入；5-i/o输出；
     */
    private Integer channelType;

    /**
     * 码流类型:1-主码流；2-子码流
     */
    private Integer streamType;

    /**
     * 最大看守位数:通道允许的最大看守位数量
     */
    private Integer maxGuard;

    /**
     * 最大预置点数:通道允许的最大预置点数量
     */
    private Integer maxPreset;

    /**
     * 最大巡航路径数量:通道允许的最大巡航路径数量
     */
    private Integer maxPatrol;

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

}
