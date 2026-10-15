package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 轨迹信息
 */
@Data
public class TrackArrowInfo implements Serializable {
    private String len;
    private String mmsi;
    private String lastTm;
    private String latitude;
    private String sclass;
    private String course;
    private String id;
    private String terminal;
    private String staticState;
    private String speed;
    private String lastDt;
    private String longitude;
}
