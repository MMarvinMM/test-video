package com.chinatower.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @desription: 图层类型对应颜色
 * @data 创建时间：2024/1/5 9:31
 * @author chuan hao
 * @version 1.0
 *
 */
@Getter
@AllArgsConstructor
public enum PgLayerStyleEnums {
    POINT("POINT","点","layer_style_point","point"),
    MULTILINESTRING("MULTILINESTRING","线","layer_style_line","line"),
    MULTIPOLYGON("MULTIPOLYGON","多边形","layer_style","polygon"),
    MULTIPOINT("MULTIPOINT","点","layer_style_point","point"),
    LINESTRING("LINESTRING","线","layer_style_line","line"),
    POLYGON("POLYGON","多边形","layer_style","polygon");
    /**
     * 图层类型
     */
    private String name;
    /**
     * 描述
     */
    private String desc;
    /**
     * 样式名称
     */
    private String styleName;
    /**
     * 样式类型
     */
    private String geoType;

    public static PgLayerStyleEnums get(String name){
        for (PgLayerStyleEnums enums : PgLayerStyleEnums.values()){
            if (enums.getName().equals(name)){
                return enums;
            }
        }
        return null;
    }
}
