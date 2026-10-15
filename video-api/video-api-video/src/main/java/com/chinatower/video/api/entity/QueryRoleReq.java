package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryRoleReq extends VideoBaseReq{
    private static final long serialVersionUID = 1L;
    /**
     * 角色编码
     */
    private String roleId;
    private String roleName;//角色名称
    private String roleStatus;//角色状态 ——1正常  0 停用
    private String tenantId;//租户编码
    private String roleDescribe;//角色描述
    private String roleFlag;//是否为超级管理员 1 是 0 否
    private String creator;//创建人
    private String creatorDate;//创建时间
    private String modifier;//修改人
    private String modifierDate;//创建时间
    private String roleType;//角色类型 1 用户角色  2 租户角色  3 系统角色  4 普通角色
    private String platformConfiguration;//是否具有平台配置权限 0是 1否
    private String cloudControl;//是否具有云台操控权限  0是 1否

}
