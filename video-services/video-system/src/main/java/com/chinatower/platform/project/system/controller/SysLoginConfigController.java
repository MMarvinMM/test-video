package com.chinatower.platform.project.system.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.common.constant.UserConstants;
import com.chinatower.platform.common.utils.FileUtils;

import com.chinatower.platform.common.utils.poi.ExcelUtil;

import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.project.system.domain.SysLoginConfig;
import com.chinatower.platform.project.system.service.ISysLoginConfigService;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * video-system
 * 【登录配置】登录页标题图片配置
 *
 * @author 王梦迪
 * @date 2021-11-15
 */
@Api(tags = "登录页标题图片配置")
@RestController
@RequestMapping("/system/loginConfig")
public class SysLoginConfigController extends TowerBaseController
{
    @Autowired
    private ISysLoginConfigService sysLoginConfigService;

    /**
     * 查询【登录配置】列表
     */
    @ApiOperation(value = "查询【登录配置】列表")
    @GetMapping("/list")
    public TableDataInfo list(SysLoginConfig sysLoginConfig)
    {
        startPage();
        List<SysLoginConfig> list = sysLoginConfigService.selectSysLoginConfigList(sysLoginConfig);
        return getDataTable(list);
    }


    /**
     * 导出【登录配置】列表
     */
    @ApiOperation(value = "导出【登录配置】列表")
    @TowerLog(title = "【登录配置】", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysLoginConfig sysLoginConfig, HttpServletResponse response)
    {
        List<SysLoginConfig> list = sysLoginConfigService.selectSysLoginConfigList(sysLoginConfig);
        byte[] data =new ExcelUtil<>(SysLoginConfig.class).exportBytes(list);
        FileUtils.download(response, data);
    }

    /**
     * 获取【登录配置】详细信息
     */
    @ApiOperation(value = "获取【登录配置】详细信息")
    @GetMapping(value = "/detail/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(sysLoginConfigService.selectSysLoginConfigById(id));
    }

    /**
     * 新增【登录配置】
     */
    @ApiOperation(value = "新增【登录配置】")
    @TowerLog(title = "【登录配置】", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public Result add(@RequestBody SysLoginConfig sysLoginConfig)
    {
        if (UserConstants.NOT_UNIQUE.equals(sysLoginConfigService.checkLoginTitleUnique(sysLoginConfig.getTitle()))) {
            return Result.fail("新增标题为'" + sysLoginConfig.getTitle() + "'的配置失败，标题已存在");
        }
        return toAjax(sysLoginConfigService.insertSysLoginConfig(sysLoginConfig));
    }

    /**
     * 修改【登录配置】
     */
    @ApiOperation(value = "修改【登录配置】")
    @TowerLog(title = "【登录配置】", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/update")
    public Result edit(@RequestBody SysLoginConfig sysLoginConfig)
    {
        return toAjax(sysLoginConfigService.updateSysLoginConfig(sysLoginConfig));
    }

    /**
     * 删除【登录配置】
     */
    @ApiOperation(value = "删除【登录配置】")
    @TowerLog(title = "【登录配置】", businessType = BusinessType.DELETE)
	@GetMapping("/del/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(sysLoginConfigService.deleteSysLoginConfigByIds(ids));
    }

    /**
     * 状态修改
     */
    @ApiOperation(value = "状态修改")
    @TowerLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public Result changeStatus(@RequestBody SysLoginConfig sysLoginConfig) {
        sysLoginConfig.setModifier(SecurityUtils.getUsername());
        return toAjax(sysLoginConfigService.updateSysLoginConfig(sysLoginConfig));
    }
}
