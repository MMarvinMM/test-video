package com.chinatower.gis.api.entity.gis;

import lombok.Data;

@Data
public class GisVegetationDataRes {
    /**
     * 可燃物编号/林种  1：针叶林；2阔叶林；3针阔混交林；4竹林；5特灌
     */
    private Integer fuelModelNumber;
    /**
     * 郁闭度  0-1之间变化，林地内树冠的垂直投影面积与林地面积之比
     */
    private Double plantsDensity;
    /**
     * 温度植被干旱指数    0-1之间变化，值越大越干旱
     */
    private Double plantsDegreeOfDrought;
}
