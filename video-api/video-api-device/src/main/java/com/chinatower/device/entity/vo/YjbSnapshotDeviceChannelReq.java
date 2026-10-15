package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 应急部抓图任务中的设备通道组合。
 */
@Data
public class YjbSnapshotDeviceChannelReq implements Serializable {

    /** 摄像机编码。 */
    private String deviceCode;

    /** 通道编码。 */
    private String channelCode;
}
