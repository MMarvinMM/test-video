package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InspectionConfStatisticsVO extends BaseDataScopeEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 巡查计划总数量
     */
    private int allNum;

    /**
     * 创建开始时间
     */
    private String createStartDate;
    /**
     * 创建结束时间
     */
    private String createEndDate;
}
