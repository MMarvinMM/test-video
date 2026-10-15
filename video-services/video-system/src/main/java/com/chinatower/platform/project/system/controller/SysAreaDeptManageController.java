package com.chinatower.platform.project.system.controller;

import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.project.system.service.ISysAreaDeptManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * video-system
 * (SysAreaDeptManage)表控制层
 *
 * @author zs
 * @since 2021-03-14 12:03:37
 */
@Api(tags = " 组织地市管理")
@RestController
@RequestMapping("/system/sysAreaDeptManage")
public class SysAreaDeptManageController {
    /**
     * 服务对象
     */
    @Resource
    private ISysAreaDeptManageService sysAreaDeptManageService;

    /**
     * 根据组织市id获取县级管理区域
     */
    @ApiOperation(value = "根据组织市id获取县级管理区域")
    @GetMapping("selectManageByDeptCityId")
    public AjaxResult selectManageByDeptCityId(String deptCityId) {
        return AjaxResult.success(sysAreaDeptManageService.selectManageByDeptCityId(deptCityId));
    }

    /**
     * 根据行政县id获取管理区域
     */
    @ApiOperation(value = "根据行政县id获取管理区域")
    @GetMapping("selectCountyByManageId")
    public AjaxResult selectCountyByManageId(String manageId) {
        return AjaxResult.success(sysAreaDeptManageService.selectCountyByManageId(manageId));
    }


}
