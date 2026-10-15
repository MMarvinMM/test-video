package com.chinatower.platform.project.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_role_data_rel")
public class SysRoleDataRel {

    private Long roleId;

    private String dataScope;

    private String tenantId;

    private String industryCode;

    private String appVerCode;

}
