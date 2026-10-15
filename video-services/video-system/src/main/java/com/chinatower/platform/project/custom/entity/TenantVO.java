package com.chinatower.platform.project.custom.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 租户订购关系管理参数
 */
@Data
@ToString
public class TenantVO extends BaseDataScopeEntity {

    private static final long serialVersionUID = 1L;
    private String tenantId;
    private String tenantName;
    private String clientId;
    private String clientName;
    private String industryType;
    private String linkMan;
    private String linkPhone;
    private String tenantManager;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date endTime;
    private String provinceId;
    private String cityId;
    private String countyId;
    private String streetId;
    private String creator;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private String creatorDate;
    private String modifier;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date modifierDate;
//    private String deleteFlag;
    private Integer pageNum;
    private Integer pageSize;
    private String operateFlag;//操作标识：1编辑2生失效
    private String orgId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private String startDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private String endDate;
    private String custCode;
    private String keyword;
    private List<String> tenantList;
    private String source;//客户来源

    //行业编码
    private String industryCode;

    //行业版本编码
    private String appVerCode;

    //应用版本名称
    private String appVerName;

    //应用版本描述
    private String appVerDesc;

    /**
     * 登录类型（0：默认类型 1：自定义）
     */
    private String loginType;

    /**
     * 登陆地址
     */
    private String loginUrl;

}