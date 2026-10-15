package com.chinatower.outdata.api.entity;

import lombok.Data;

@Data
public class ShipPortraitAisResultVO {

    private String msg;

    private String code;

    private RadarShipPortraitAisRsp data;

}
