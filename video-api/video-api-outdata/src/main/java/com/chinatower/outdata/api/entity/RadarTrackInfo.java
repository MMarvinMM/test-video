package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RadarTrackInfo implements Serializable {

    /**
     * 船舶MMSI: 船舶9位码
     */
    private String mmsi;

    /**
     * 目标id
     */
    private String targetId;

    /**
     * 船舶信息
     */
    private List<RadarReviewInfo> points;
}
