package com.chinatower.platform.modules.alarm.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @className: RoleDataScopeVo
 * @description: 用户角色数据权限表
 * @author: yongqi.han
 * @date: 2022/10/18
 * @requestID 需求单号及需求名称
 */
@Data
public class UserDataScopeVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一id
     */
    private String userId;
    /**
     * 用户组织
     */
    private String deptId;
    /**
     * 租户编码
     */
    private String tenantId;
    /**
     * 行业编码
     */
    private String industryCode;
    /**
     * 行业版本编码
     */
    private String appVerCode;
    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）
     */
    private String dataScope;
    /**
     * 角色：自定数据权限 关联组织信息
     */
    private String customDepts;

}
