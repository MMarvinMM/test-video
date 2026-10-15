package com.chinatower.gis.modules.gis.entity.layer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LayerTypeAndName {
    /**
     * 表名
     * */
    private String layerName;
    /**
     * 表类型简拼
     * */
    private String layerType;
    /**
     * 表类型名称
     * */
    private String layerTypeName;
    /**
     * 配置的面积字段
     * */
    private String areaColName;
}