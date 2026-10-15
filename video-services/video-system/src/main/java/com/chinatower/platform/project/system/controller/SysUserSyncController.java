package com.chinatower.platform.project.system.controller;

import com.chinatower.common.core.web.domain.ApiResult;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.project.system.domain.dto.SyncUserDTO;
import com.chinatower.platform.project.system.service.ISysUserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * video-system
 *
 */
@RestController
@RequestMapping("/system")
public class SysUserSyncController extends TowerBaseController {
    @Autowired
    private ISysUserService userService;

    /**
     * 同步应用平台客户账号信息
     */
    @ApiOperation(value = "同步应用平台客户账号信息")
    @PostMapping("/synUserInfo")
    public ApiResult<String> synUserInfo(@RequestBody SyncUserDTO param) {
        if (Objects.isNull(param)) {
            return ApiResult.FAIL("请求参数不能为空");
        }
        return userService.synUserInfo(param);
    }
}
