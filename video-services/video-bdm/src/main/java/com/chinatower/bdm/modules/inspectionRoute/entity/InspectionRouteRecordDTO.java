package com.chinatower.bdm.modules.inspectionRoute.entity;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class InspectionRouteRecordDTO implements Serializable {

    private Long id;

    private Long routeId;

    private Date StartTime;

    private Date endTime;

    private Long duration;

    private Integer routeStatus;

    private String tenantId;

    private String IndustryCode;

    private String appVerCode;

    private String creator;

    private String modifier;

    private Date createTime;

    private Date updateTime;

    private Integer deleteFlag;
}
