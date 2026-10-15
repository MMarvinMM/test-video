package com.chinatower.manage.api.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SysRoleDataRelEdition {

    private Long editionId;
    /** 角色ID */
    private Long roleId;
    /** 租户id */
    private String tenantId;
    /** 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限） */
    private Long dataScope;
    /** 行业应用版本编码 */
    private String appVerCode;
    /** 行业编码 */
    private String industryCode;

    private Long localizedVersionId;

    /** 创建人 */
    private String creator;

    /** 创建时间 */
    private Date createTime;
}
