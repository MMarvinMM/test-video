package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

@Data
public class InspectionRouteProcessParam extends BaseDataScopeEntity {

    /**
     * 巡检路线编码
     * @required true
     */
    private Long inspectionRouteId;

    /**
     * 巡检路线下具体某一点位编码
     * @required true
     */
    private Long inspectionRouteSubId;

    /**
     * 巡检状态 1-开始 2-更新 3-暂停 4-恢复 5-重置 6-结束
     */
    private Integer status;
}
