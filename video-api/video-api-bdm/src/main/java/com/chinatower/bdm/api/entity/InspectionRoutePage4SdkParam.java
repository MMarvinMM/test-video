package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

@Data
public class InspectionRoutePage4SdkParam extends BaseDataScopeEntity {

    /**
     * 页码
     * @required true
     */
    private Integer pageNum;

    /**
     * 每页条数
     * @required true
     */
    private Integer pageSize;

    /**
     * 巡检路线名称
     */
    private String inspectionRouteName;
}
