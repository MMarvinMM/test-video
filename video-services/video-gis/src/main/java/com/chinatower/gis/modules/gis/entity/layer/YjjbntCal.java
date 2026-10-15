package com.chinatower.gis.modules.gis.entity.layer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

/**
 * @desription: 套合分析的有效图层
 *
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YjjbntCal {
    private String bsm;
    private double calArea;
    private double coverRatio;
    private String tbName;
    /*
     * 0 ST_Overlaps，相交关系
     * 1 ST_Contains，包含关系
     * 2 st_intersects, 相交关系
     * 3 ST_Contains，被包含关系
     * */
    private int relationType;
}
