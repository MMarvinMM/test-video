package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 轨迹信息
 */
@Data
public class ThermalMapInfo implements Serializable {
    private String id;
    private String lastTm;
    private String speed;
    private String longitude;
    private String latitude;
    private String mmsi;
    private String len;
    private String course;
    private String lastDt;
    private String staticState;
    private String terminal;
    private String durationMinutes;
    private String sclass;

}
