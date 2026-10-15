package com.chinatower.platform.project.custom.entity;

import com.chinatower.platform.framework.web.domain.SystemBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 组织管理参数
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustOrganVO extends SystemBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String orgId;//组织机构编码
    
    private String orgName;//组织机构名称
    
    private String tenantId;//租户编码
    
    private String orgSource;//组织机构来源
    
    private String parentOrgId;//上级机构编码
    
    private String provinceId;//省份

    private String cityId;//地市

    private String countyId;//区县

    private String streetId;//街道

    private String orgDesc;//组织描述

    private String orgLevel;//组织层级

    private String parentOrgName;//上级机构名称

    private String creator;//创建人

    private String creatorDate;//创建时间

    private String modifier;//修改人

    private String modifierDate;//修改时间
    //
    private String deleteFlag;//0正常 1删除 2失效

    private String flag;// 1行政区划

    private String parentAreaId;//上级行政区划


    private List<OrganAreaVO> areaInfo;//管理区域

    private List<OrganDeviceVO> deviceInfo;//设备权限

    private String orgType;//组织类型：1组织 2部门

    private String keyword;
    private Integer pageNum;
    private Integer pageSize;
    private String parentOrgLevel;

    private Set<String> orgIdList;
    private List<String> areaCodes;
    private String isChange;//能否被选中

    private String orgIdStr;//组织机构编码

    private Boolean isChecked=false;


}
