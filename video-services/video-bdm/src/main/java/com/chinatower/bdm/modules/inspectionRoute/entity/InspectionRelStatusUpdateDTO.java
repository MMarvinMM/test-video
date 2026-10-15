package com.chinatower.bdm.modules.inspectionRoute.entity;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class InspectionRelStatusUpdateDTO implements Serializable {

    /**
     * 巡检开始时间
     */
    private Date inspectionStartTime;

    /**
     * 巡检暂停时间
     */
    private Date pauseStartTime;

    /**
     * 巡检更新状态
     */
    private Integer routeStatus;

    /**
     * 巡检原有状态
     */
    private Integer originalRouteStatus;

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
