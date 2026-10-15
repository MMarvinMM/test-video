package com.chinatower.sync.consumer.item;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 4.25.设备状态同步-到其他系统-队列推送 请求实体类
 */
@Data
public class AIotDeviceStatusSync implements Serializable {

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 设备编码，设备的唯一编码
     */
    private String deviceCode;

    /**
     * 设备名称，设备的名称
     */
    private String deviceName;

    /**
     * 设备状态，设备状态变化时必传，0离线，1在线，2从未连接
     */
    private Integer onlineStatus;

    /**
     * 业务状态，0离线，1空闲，2工作中，4故障
     */
    private Integer deviceStatus;

    /**
     * 通道状态信息列表
     */
    private List<AIotDeviceChannelStatus> basicDeviceChannel;

    /**
     * 上报时间，格式为：yyyy-MM-dd HH:mm:ss
     */
    private String updateTime;

    /**
     * 站址编码
     */
    private String siteCode;

    /**
     * 属地编码，视联类不为空
     */
    private String colonyCode;

    /**
     * 设备类型（三级）
     */
    private String deviceType;

    /**
     * 产品编码
     */
    private String productionCode;

    /**
     * 平台编码
     */
    private String platformCode;

    /**
     * 来源版本，数据新增或者更新
     */
    private Long sourceVersion;

    /**
     * 扩展字段，json结构的扩展信息，需要和设备状态一起上报的数据
     */
    private String ext;

}