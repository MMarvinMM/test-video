package com.chinatower.bdm.modules.inspectionRoute.entity;


import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class InspectionDTO extends BaseDataScopeEntity implements Serializable {

    /**
     * 巡检线路ID
     */
    private Long inspectionRouteId;

    /**
     * 巡检路线状态
     */
    private String routeStatus;

    /**
     * 巡检路线名称
     */
    private String inspectionRouteName;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 创建时间开始
     */
    private String createTimeBegin;

    /**
     * 创建时间结束
     */
    private String createTimeEnd;

    /**
     * 修改时间开始
     */
    private String updateTimeBegin;

    /**
     * 修改时间结束
     */
    private String updateTimeEnd;

    /**
     * 当前页
     */
    private Integer pageNum;

    /**
     * 每页条数
     */
    private Integer pageSize;

    /**
     * 排序字段
     */
    private String sortName;

    /**
     * 排序规则
     */
    private String sort;


}
