package com.chinatower.sync.consumer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-17
 */
@Getter
@Setter
@TableName("vm_channel_info_t")
public class ChannelInfoDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "channel_id", type = IdType.AUTO)
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
    private Date creatorDate;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    private Date modifierDate;

    /**
     * 0正常1删除
     */
    private String deleteFlag;


}
