package com.chinatower.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @desription: 空间查询类型
 * @data 创建时间：2024/1/30 15:48
 * @author chuan hao
 * @version 1.0
 *
 */
@Getter
@AllArgsConstructor
public enum SpaceQueryTypeEnums {
    ZERO(0,"ST_intersects"),
    ONE(1," ST_Contains");

    /**
     * 类型
     */
    private Integer type;
    /**
     * 条件类型
     */
    private String join;

    public static SpaceQueryTypeEnums get(Integer type){
        for (SpaceQueryTypeEnums tempEnums : SpaceQueryTypeEnums.values()){
            if (tempEnums.getType().equals(type)){
                return tempEnums;
            }
        }
        return ZERO;
    }
}
