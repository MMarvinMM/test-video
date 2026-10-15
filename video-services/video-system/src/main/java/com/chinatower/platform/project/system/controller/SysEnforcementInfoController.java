package com.chinatower.platform.project.system.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.entity.admin.SysEnforcementInfo;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.project.system.service.ISysEnforcementInfoService;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * video-system
 * 用户执法信息
 *
 * @author zhang.xionghui
 */
@Api(tags = "用户执法信息")
@RestController
@RequestMapping("/system/enforcementInfo")
public class SysEnforcementInfoController extends TowerBaseController {
    @Autowired
    private ISysEnforcementInfoService sysEnforcementInfoService;

    /**
     * 获取执法用户列表
     */
    @ApiOperation(value = "获取执法用户列表")
    @GetMapping("/list")
    @TowerLog(title = "执法用户", businessType = BusinessType.SELECT)
    public TableDataInfo list(SysEnforcementInfo enforcementInfo) {
        startPage();
        enforcementInfo.setUserType("2");
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        enforcementInfo.setTenantId(currentUser.getTenantId());
        enforcementInfo.setIndustryCode(currentUser.getIndustryCode());
        enforcementInfo.setAppVerCode(currentUser.getAppVerCode());
        List<SysEnforcementInfo> list = sysEnforcementInfoService.selectEnforcementInfoList(enforcementInfo);
        return getDataTable(list);
    }


    /**
     * 根据用户编号获取详细信息
     */
    @ApiOperation(value = "根据用户编号获取详细信息")
    @GetMapping(value = { "base/detail/{userId}" })
    public AjaxResult getInfo(@PathVariable(value = "userId") String userId) {
        AjaxResult ajax = AjaxResult.success();
        SysEnforcementInfo enforcementInfo = new SysEnforcementInfo();
        if (StringUtils.isNotNull(userId)) {
            enforcementInfo.setUserId(userId);
            ajax.put(AjaxResult.DATA_TAG, sysEnforcementInfoService.selectEnforcementInfoById(enforcementInfo));
        }
        return ajax;
    }



    /**
     * 新增用户执法信息
     */
    @ApiOperation(value = "新增用户执法信息")
    @TowerLog(title = "用户执法信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/insertOrUpdateEnforcementUser")
    public Result<Integer> insertOrUpdateEnforcementUser(@RequestBody SysEnforcementInfo enforcementInfo) {
        return toAjax(sysEnforcementInfoService.insertOrUpdateEnforcementUser(enforcementInfo));
    }


}
