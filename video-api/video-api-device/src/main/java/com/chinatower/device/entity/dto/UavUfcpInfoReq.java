package com.chinatower.device.entity.dto;

import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2024/12/4 11:25
 */
@Data
public class UavUfcpInfoReq {
    private String deviceCode;
    private String timestamp;
    private UavFlyReq fly;
    private SatelliteInfo satelliteInfo;
    private UavFlyTaskRecordReq taskRecord;
    private String acceptNode;
}
