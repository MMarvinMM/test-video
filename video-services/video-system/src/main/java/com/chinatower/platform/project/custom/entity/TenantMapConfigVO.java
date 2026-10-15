package com.chinatower.platform.project.custom.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TenantMapConfigVO implements Serializable {

    private Long id;

    private String mapType;

    private String mapUrl;

    private Integer minZoom;

    private Integer maxZoom;

    private Integer sort;

    private String tenantId;

    private String acceptNode;

    private String industryCode;

    private String appVerCode;

    private String creator;

    private String modifier;

    private Integer deleteFlag;

    private Date createTime;

    private Date updateTime;
}
