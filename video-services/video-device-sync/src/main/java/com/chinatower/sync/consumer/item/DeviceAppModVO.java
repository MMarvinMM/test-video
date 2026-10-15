package com.chinatower.sync.consumer.item;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceAppModVO implements Serializable {
    /**
     * 设备编码
     */
    private String deviceCode;
    /**
     * 应用修改标识（1:已修改 0未修改）
     */
    private Integer appModFlag;

    private Long deviceVersion;

    private Long statusVersion;

    private String longitude;

    private String latitude;
}
