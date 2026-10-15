package com.chinatower.manage.api.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

@Data
public class SysRoleIndustryAuthEdition {

    /** 角色ID */
    private Long roleId;
    /** 行业权限id */
    private Long authId;
    /** 租户id */
    private String tenantId;

    @TableField(exist = false)
    private Long roleAuthId;
    /**
     * 属地版本号id
     */
    private Long localizedVersionId;

    /** 创建人 */
    private String creator;

    /** 创建时间 */
    private Date createTime;
}
