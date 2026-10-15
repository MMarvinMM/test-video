package com.chinatower.gis.api.entity.gis;

import lombok.Data;

/**
 * @desription: 火势可燃物
 * @data 创建时间：2024/6/6 11:20
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class GisFireCombustibleRes extends GisSlopeAspectRes{

    /**
     * 可燃物编号/林种  1：针叶林；2阔叶林；3针阔混交林；4竹林；5特灌
     */
    private Integer fuelModelNumber;

    /**
     * 是1小时时间内死燃料中水分的部分(百分比或分数)，以干重为基础计算。这个值也用于动态燃料模型的死草本燃料水分含量。1 h燃料含水率比100 h燃料含水率对火灾蔓延计算的影响更大。
     */
    private Double moistureOneHour;

    /**
     * 10 小时燃料水分含量
     */
    private Double moistureTenHour;

    /**
     * 100 小时燃料水分含量
     */
    private Double moistureHundredHour;
    /**
     * 是活草本和草本中水分的部分，以干重为基础计算。对于动态燃料模型来说，如果用它来确定从活草本燃料负荷类别转移到死草本燃料负荷类别的燃料量
     */
    private Double moistureLiveHerbaceous;
    /**
     * 是灌木的叶子和非常细的茎中水分的部分，以干重为基础计算。对于动态燃料模型来说，如果用它来确定从活草本燃料负荷类别转移到死草本燃料负荷类别的燃料量
     */
    private Double moistureLiveWoody;
    /**
     * 树冠覆盖百分比
     */
    private Double canopyCover;
    /**
     * 树高
     */
    private Double canopyHeight;
    /**
     * 树冠比
     */
    private Double crownRatio;
    /**
     * 郁闭度  0-1之间变化，林地内树冠的垂直投影面积与林地面积之比
     */
    private Double plantsDensity;
    /**
     * 温度植被干旱指数    0-1之间变化，值越大越干旱
     */
    private Double plantsDegreeOfDrought;

    /**
     * 火线等级
     */
    private Integer fireLevel;
}
