package com.chinatower.bdm.modules.inspectionRoute.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class InspectionVO implements Serializable {

    /**
     * 巡检路线自增id
     */
    private Long id;

    /**
     * 巡检路线名称
     */
    private String inspectionRouteName;

    /**
     * 是否暂停巡航 0-否 1-是
     */
    private Integer pauseCruiseFlag;

    /**
     * 巡检路线状态
     */
    private String routeStatus;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改时间
     */
    private String updateTime;

    private String tenantId;

    private String industryCode;

    private String appVerCode;


}
