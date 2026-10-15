package com.chinatower.outdata.api.entity;

import lombok.Data;

@Data
public class EcmwfHeader {


    private String lat1;

    private String lat2;

    private String long1;

    private String long2;

    private String nx;

    private String ny;

    private String dx;

    private String dy;

    private String min;

    private String max;

    private String gribRefTime;

    private String gribUnit;

    private String ecmwfStr;
}
