package com.chinatower.bdm.modules.inspectionRoute.entity;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class InspectionStatusUpdateDTO implements Serializable {

    /**
     * 巡检更新状态
     */
    private Integer routeStatus;

    /**
     * 巡检原有状态
     */
    private Integer originalRouteStatus;

    /**
     * 巡检原有状态列表
     */
    private List<Integer> originalRouteStatusList;

    /**
     * 更新人
     */
    private String modifier;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 主键编码
     */
    private Long id;
}
