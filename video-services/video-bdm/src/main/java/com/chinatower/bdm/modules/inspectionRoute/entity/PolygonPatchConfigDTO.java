package com.chinatower.bdm.modules.inspectionRoute.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 电子围栏查询实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PolygonPatchConfigDTO extends BaseDataScopeEntity implements Serializable {

    private String layerId;

    private String featureId;

    private List<PolygonPatchConfigDTO> polygonPatchList;
}
