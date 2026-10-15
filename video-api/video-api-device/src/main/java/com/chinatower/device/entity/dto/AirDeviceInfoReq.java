package com.chinatower.device.entity.dto;

import lombok.Data;


/**
 * @author csj
 * @description
 * @date 2024/11/4 14:37
 */
@Data
public class AirDeviceInfoReq {
    private String deviceCode;
    private String acceptNode; // 接收节点
}
