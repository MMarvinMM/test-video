package com.chinatower.industry.dto;

import lombok.Data;

/**
 * 行业应用通道
 * Created by zheng.yan on 2022/3/22.
 */
@Data
public class ChannelIndDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;
    /**
     * 通道状态，0在线，1离线，2未配置设备
     */
    private Integer channelStatus;
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

    /**
     * 外型 1-球机；2-半球；3-枪机
     */
    private Integer facade;
}
