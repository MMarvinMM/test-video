package com.chinatower.event.entity.dto;

import lombok.Data;

/**
 * @Author: songxinfeng
 * @Date: 2024/5/6 17:39
 */
@Data
public class RegionAlarmExtraField {

    /**
     * 前类型
     */
    private String preType;
    /**
     * 后类型
     */
    private String postType;

    /**
     * 图斑编号
     */
    private String spotNo;

    private String provinceName;
    /**
     * 城市名
     */
    private String cityName;
    /**
     * 区县名
     */
    private String countyName;
    /**
     * 乡镇名
     */
    private String townName;
    /**
     * 村名
     */
    private String villageName;
    /**
     * 卫片来源
     */
    private String regionSource;
    /**
     * 前时相
     */
    private String preChronology;
    /**
     * 后时相
     */
    private String postChronology;
    /**
     * 图版面积
     */
    private String spotArea;
    /**
     * 图版周长
     */
    private String spotPerimeter;
    /**
     * 变化类型
     */
    private String changeType;

    /**
     * 面积
     */
    private String area;
    /**
     * 土地利用类型
     */
    private String landUseType;
    /**
     * 影像时间
     */
    private String imageTime;
    /**
     * 是否质检
     */
    private String detectionFlag;

    /**
     * 图层id
     */
    private String layerId;

    /**
     * 图斑唯一标识
     */
    private String bsm;
}
