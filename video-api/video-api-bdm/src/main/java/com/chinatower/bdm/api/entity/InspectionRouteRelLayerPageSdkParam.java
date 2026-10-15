package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

@Data
public class InspectionRouteRelLayerPageSdkParam extends BaseDataScopeEntity {

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页条数
     */
    private Integer pageSize;

    /**
     * 巡检路线编码
     */
    private Long inspectionRouteId;

}
