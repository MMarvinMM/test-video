package com.chinatower.platform.project.system.domain.vo;

import com.chinatower.platform.entity.admin.SysRole;
import com.chinatower.platform.project.system.domain.SysRoleIndustryAuth;
import com.chinatower.system.api.model.SysRoleScreenVO;
import lombok.Data;

import java.util.List;

@Data
public class SaveRoleParam extends SysRole {

    List<SysRoleIndustryAuth> roleIndustryAuths;

    List<SysRoleScreenVO> sysRoleScreenVOList;

    /**
     * 新增修改标识(1.新增 2.修改)
     */
    private String flag;

}
