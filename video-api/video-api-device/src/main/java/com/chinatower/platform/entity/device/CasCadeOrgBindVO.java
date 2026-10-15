package com.chinatower.platform.entity.device;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CasCadeOrgBindVO implements Serializable {

    private Long id;

    private String tenantId;

    private String platCode;

    private String orgCode;

    private String orgName;

    private String parentOrgCode;

    private String creator;

    private String modifier;

    private Date createTime;

    private Date updateTime;

}
