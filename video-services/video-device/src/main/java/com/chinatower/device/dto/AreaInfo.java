package com.chinatower.device.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AreaInfo implements Serializable {

    /**
     * 区域ID--主键
     */
    private Long areaId;

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 多边形：polygon  线性：lineString（只涉及到过线报警）
     */
    private String areaShape;

    /**
     * areaShape是polygon时传参（84坐标系），圆形  按照64边型坐标
     */
    private String polygonCoords;

    /**
     * areaShape是lineString  传参
     */
    private String lineCoords;
}
