package com.chinatower.airline.dto.request;

import lombok.Data;

@Data
public class RoadsNameMilepostReq {

    /**
     * 照片id
     */
    private String photoId;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;
}
