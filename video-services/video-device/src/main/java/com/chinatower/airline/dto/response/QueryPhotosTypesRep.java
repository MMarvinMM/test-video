package com.chinatower.airline.dto.response;

import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2025/7/16 16:22
 */
@Data
public class QueryPhotosTypesRep {
    private String photoId;
    private String longitude;
    private String latitude;
    private String photoUrl;
    private String deviceCode;
    private String isCollect;
    private String photoName;
    private String timeCreated;
    private String type; //0摄像机 1无人机
    private String collectType;
}
