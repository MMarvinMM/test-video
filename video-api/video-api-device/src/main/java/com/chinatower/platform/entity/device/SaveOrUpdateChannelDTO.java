package com.chinatower.platform.entity.device;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName DeviceInfoDTO
 * @Description 设备通道请求实体类
 * @Author chensw
 * @Date 2022/1/18
 */
@Data
public class SaveOrUpdateChannelDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
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
