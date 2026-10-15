package com.chinatower.platform.project.system.controller;

import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.project.system.service.ISysMessagePermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * video-system
 * @ClassName SysMessagePermissionController
 * @Description 用户消息权限管理
 * @Author D
 * @Date 2022/2/28 20:07
 */
@Api(tags = "用户消息权限管理")
@RestController
@RequestMapping("/system/message/permission")
public class SysMessagePermissionController {

    @Autowired
    private ISysMessagePermission sysMessagePermission;

    /**
     * 根据用户ID获取详细信息
     */
    @ApiOperation(value = "根据菜单编号获取详细信息")
    @GetMapping(value = "/detail/{userId}")
    public AjaxResult getInfo(@PathVariable String userId)
    {
        return AjaxResult.success(sysMessagePermission.selectPermissionById(userId));
    }

}
