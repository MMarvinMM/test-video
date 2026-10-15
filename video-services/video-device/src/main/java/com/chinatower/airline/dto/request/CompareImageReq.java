package com.chinatower.airline.dto.request;

import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2025/7/16 15:19
 */
@Data
public class CompareImageReq {
    private String url;
    private String id;
    private String photoId;
    private String deviceCode;
    private String deviceType;
    private String collectType;
    private String tenantId;
    private String industryCode;
    private String appVerCode;
    private String userId;
    private String longitude;
    private String latitude;
    private String timeCreated;
    private String photoName;
    private String photoType;
}
