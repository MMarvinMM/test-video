package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * AIS船舶画像返回实体类
 */
@Data
public class RadarRegionalOccurrenceBehavior implements Serializable {

    /**
     * 区域ID
     */
    private String areaId;

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 是否经过
     */
    private Boolean occurrence;

}
