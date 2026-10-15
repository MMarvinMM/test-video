package com.chinatower.platform.entity.device;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 级联平台绑定摄像机下发信息
 */
@Data
public class CasCadeBindDeviceInfoVO implements Serializable {
    /**
     * 平台编码
     */
    private String platCode;
    /**
     * 设备编码
     */
    private String deviceCode;
    /**
     * 通道编码
     */
    private String channelCode;
    /**
     * 自定义通道编码
     */
    private String customChannelCode;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 创建时间
     */
    private Date createTime;
}
