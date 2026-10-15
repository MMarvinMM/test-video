package com.chinatower.airline.dto.request;

import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2025/3/7 10:37
 */
@Data
public class AirResultCollectReq {
    private String collectId;
    private String type;
    private String userId;
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
    private String industryCode;
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
