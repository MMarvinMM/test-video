package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LayerAreaVO extends BaseDataScopeEntity {

    /**
     * 图层类别
     */
    private String layerTypeId;
    /**
     * 图层名称
     */
    private String layerTypeName;
    /**
     * 面积
     */
    private String area;

    /**
     * 图层类别简称
     */
    private String abbreviation;
}
