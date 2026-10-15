package com.chinatower.platform.project.system.domain.vo;

import com.chinatower.platform.entity.admin.SysRole;
import lombok.Data;

@Data
public class SysRoleQuery extends SysRole {

    private String appVerCode;

    private String tradeInfo;

    private String tenantSearch;

}
