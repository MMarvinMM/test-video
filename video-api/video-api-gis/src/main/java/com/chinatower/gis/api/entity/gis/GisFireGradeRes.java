package com.chinatower.gis.api.entity.gis;

import lombok.Data;

/**
 * @desription: 火灾等级
 * @data 创建时间：2024/6/6 11:24
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class GisFireGradeRes extends GisSlopeAspectRes{

    /**
     * 林种，1为针叶林；2为阔叶林；3为混交林；4为竹林；5为特灌
     */
    private Integer plantsSpecies;
    /**
     * 郁闭度  0-1之间变化，林地内树冠的垂直投影面积与林地面积之比
     */
    private Double plantsDensity;
    /**
     * 温度植被干旱指数    0-1之间变化，值越大越干旱
     */
    private Double plantsDegreeOfDrought;
    /**
     * 海拔
     */
    private Integer altitude;
    /**
     * 火线等级
     */
    private Integer fireLevel;
}
