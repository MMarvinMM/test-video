package com.chinatower.platform.project.system.domain;

import lombok.Data;

/**
 * 角色和行业关联
 *
 * @author 王梦迪
 */
@Data
public class SysRoleIndustryAuth
{
    /** 角色ID */
    private Long roleId;

    /** 行业权限id*/
    private String authId;

    /** 租户id */
    private String tenantId;


}
