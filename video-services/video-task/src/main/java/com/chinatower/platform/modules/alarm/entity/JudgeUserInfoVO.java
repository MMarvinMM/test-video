package com.chinatower.platform.modules.alarm.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * vm_userinfo_t
 * @author
 */
@Data
public class JudgeUserInfoVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一id
     */
    private String userId;


    /**
     * 所属组织机构
     */
    private String orgId;

    /**
     * 是否是所有数据权限 1所有数据权限
     */
    private String dataScope;


    /**
     * 移动电话
     */
    private String mobilePhone;

    /**
     * 固定电话
     */
    private String phone;


    /**
     * 租户编码
     */
    private String tenantId;

    /**
     * 租户角色判断：1为租户管理员 2 为租户管理员角色 4为普通研判角色
     */
    private String roleId;
    /**
     * 用户角色聚合
     */
    private String roles;
    /**
     * 行业编码
     */
    private String industryCode;
    /**
     * 行业版本编码
     */
    private String appVerCode;


    private List<String> deviceList;
    private String deviceCodeStr; //摄像机列表拼接串
    private String regionCodeStr; //区域列表拼接串

    private String parentOrgId; //上级组织

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备类型
     */
    private String deviceType;
}
