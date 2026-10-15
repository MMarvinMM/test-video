package com.chinatower.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @desription: 地块查询条件类型
 * @data 创建时间：2024/1/23 17:59
 * @author chuan hao
 * @version 1.0
 *
 */
@Getter
@AllArgsConstructor
public enum LayerParcelQuerySearchTypeEnums {

    ONE(1," and "),
    TWO(2," or ");

    /**
     * 类型
     */
    private Integer type;
    /**
     * 条件类型
     */
    private String join;

    public static LayerParcelQuerySearchTypeEnums get(Integer type){
        for (LayerParcelQuerySearchTypeEnums tempEnums : LayerParcelQuerySearchTypeEnums.values()){
            if (tempEnums.getType().equals(type)){
                return tempEnums;
            }
        }
        return null;
    }
}
