package com.chinatower.system.api.resp;

import lombok.Data;

/**
 * 角色和部门关联 sys_role_dept返回对象
 *
 * @author iwhalecloud
 */
@Data
public class SysRoleDeptResponse {
    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 部门ID
     */
    private String deptId;

    /**
     * 租户ID
     */
    private String tenantId;
}
