package com.chinatower.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @desription: 图层地块查询值类型
 * @data 创建时间：2024/1/24 9:07
 * @author chuan hao
 * @version 1.0
 *
 */
@Getter
@AllArgsConstructor
public enum LayerParcelQueryValueTypeEnums {

    ONE(1,"=%s"),
    TWO(2," like %s"),
    THREE(3," not like %s"),
    FOUR(4," != %s"),
    FIVE(5," <= %s"),
    SIX(6," > %s"),
    SEVEN(7," >= %s"),
    EIGHT(8," < %s"),
    NINE(9," in (%s)"),
    TEN(10," not in (%s)");

    /**
     * 类型
     */
    private Integer type;
    /**
     * 条件类型
     */
    private String join;


    public static LayerParcelQueryValueTypeEnums get(Integer type){
        for (LayerParcelQueryValueTypeEnums tempEnums : LayerParcelQueryValueTypeEnums.values()){
            if (tempEnums.getType().equals(type)){
                return tempEnums;
            }
        }
        return null;
    }
}
