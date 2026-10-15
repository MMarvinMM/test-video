package com.chinatower.gis.api.entity.layer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LayerHasCustomBsmFieldRes {
    /**
     * 该表的记录数
     * */
    private int rowCount;
    /**
     * 第一条记录的长度
     * */
    private int fieldLength;
    /**
     * 是否是自定义bsm字段
     * */
    private boolean isCustom;
}
