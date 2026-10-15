package com.chinatower.industry.vo;

import lombok.Data;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: IndChannelInfoVO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/4/3 16:29
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class IndChannelInfoVO {

    /**
     * 通道id
     */
    private Long channelId;

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
     * 通道状态，0在线，1离线，2未配置设备
     */
    private Integer channelStatus;

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
     * 外型 1-球机；2-半球；3-枪机
     */
    private Integer facade;

    /**
     * 通道类型 1-数字信号；2-模拟信号；3-音频信号；4-i/o输入；5-i/o输出；
     */
    private Integer channelType;

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
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private String creatorDate;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    private String modifierDate;

    /**
     * 0正常1删除
     */
    private String deleteFlag;
}
