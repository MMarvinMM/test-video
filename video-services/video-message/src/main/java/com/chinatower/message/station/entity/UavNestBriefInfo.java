package com.chinatower.message.station.entity;


import lombok.Data;


@Data
public class UavNestBriefInfo {

    private String deviceCode;

    private Integer positionFix;

    private String longitude;

    private String latitude;
}
