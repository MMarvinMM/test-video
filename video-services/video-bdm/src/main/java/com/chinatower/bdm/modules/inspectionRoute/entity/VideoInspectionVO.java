package com.chinatower.bdm.modules.inspectionRoute.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class VideoInspectionVO implements Serializable {

    /**
     * 巡检路线自增id
     */
    private Long id;

    private Long inspectionRouteId;

    private String inspectionRouteName;

    /**
     * 是否暂停巡航 0-否 1-是
     */
    private Integer pauseCruiseFlag;

    private Integer routeStatus;

    private Integer deviceNum;

    private String totalDurationTime;

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
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    private String tenantId;

    private String industryCode;

    private String appVerCode;

    private Integer deleteFlag;
}
