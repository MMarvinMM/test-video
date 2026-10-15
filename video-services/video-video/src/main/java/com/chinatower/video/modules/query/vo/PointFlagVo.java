package com.chinatower.video.modules.query.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointFlagVo implements Serializable {
    /**
     * 自定义字段，前端传什么，回传什么
     */
    private String flag;

    /**
     * 1-区域 2-绊线 3-管线 4-文字
     */
    private String type;

    /**
     * 点位json串
     */
    private String points;

    /**
     * 点位json串(经纬度)
     */
    private String pointsLonLat;

}
