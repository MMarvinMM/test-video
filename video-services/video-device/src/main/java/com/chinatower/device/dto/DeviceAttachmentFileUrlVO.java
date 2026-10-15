package com.chinatower.device.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 设备编码附件信息
 */
@Data
public class DeviceAttachmentFileUrlVO implements Serializable {

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 附件信息
     */
    private List<String> fileUrlList;
}
