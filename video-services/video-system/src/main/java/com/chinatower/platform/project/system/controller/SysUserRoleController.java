package com.chinatower.platform.project.system.controller;

import cn.hutool.core.bean.BeanUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.project.system.domain.SysUserRole;
import com.chinatower.platform.project.system.domain.vo.EditDefaultRoleParam;
import com.chinatower.platform.project.system.service.ISysUserRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * video-system
 *
 */
@Api(tags = "角色信息")
@RestController
@RequestMapping("/system/userRole")
public class SysUserRoleController {

    @Autowired
    ISysUserRoleService userRoleService;

    /**
     * 根据角色编号获取详细信息
     */
    @ApiOperation(value = "关联用户的角色")
    @PostMapping(value = "/saveUserRole")
    public Result save(@RequestBody EditDefaultRoleParam param)
    {
        userRoleService.save(BeanUtil.toBean(param, SysUserRole.class));
        return Result.success("关联成功");
    }

}
