package com.chinatower.platform.enums;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @desription: 图层类型字段类型
 * @data 创建时间：2024/1/18 9:48
 * @author chuan hao
 * @version 1.0
 *
 */
@Getter
@AllArgsConstructor
public enum SysLayerTypeFieldsTypeEnums {
    ZERO(0,"text COLLATE pg_catalog.\"default\","),
    ONE(1,"integer,"),
    TWO(2,"double precision,"),
    THREE(3,"character varying(%s) COLLATE pg_catalog.\"default\","),
    FOUR(4,"timestamp(6),");
    private Integer type;

    private String value;

    public static SysLayerTypeFieldsTypeEnums get(Integer type){
        for (SysLayerTypeFieldsTypeEnums tempEnums : SysLayerTypeFieldsTypeEnums.values()){
            if (tempEnums.getType().equals(type)){
                return tempEnums;
            }
        }
        return null;
    }
}
