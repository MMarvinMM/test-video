package com.chinatower.device.entity.dto;

import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2025/3/10 14:21
 */
@Data
public class CollectReq {
    private String accessNode;
    private String id;
    private String name;
    private String url;
    private String lenType;
    private String planTime;
    private String deviceCode;
    private String taskRecordId;
    private String mediumCreateTime;
    private String taskRecordName;
    private String uavCode;
    private String dataType;
    private String longitude;
    private String latitude;
    private String altitude;
    private String height;
    private String watermarkUrl;
    private String isWatermark;
    private String remark;
    /**
     * 道路信息
     */
    private String roadInfo;
}
