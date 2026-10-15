package com.chinatower.platform.project.system.controller;

import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.project.system.domain.SysSso;
import com.chinatower.platform.project.system.service.ISysSsoService;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * video-system
 * Api接口管理
 *
 * @author iwhalecloud
 */
@Api(tags = "Api接口管理")
@RestController
@RequestMapping("/system/sso")
public class SysSsoController extends TowerBaseController {
    @Autowired
    private ISysSsoService ssoService;

    /**
     * 获取API配置列表
     */
    @ApiOperation(value = "获取API配置列表")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody SysSso vo) {
        startPage();
        List<SysSso> list = ssoService.selectSsoList(vo);

        return getDataTable(list);
    }


    /**
     * 根据参数编号获取详细信息
     */
    @ApiOperation(value = "根据参数编号获取详细信息")
    @PostMapping(value = "/detail")
    public AjaxResult getInfo(@RequestBody SysSso vo) {
        return AjaxResult.success(ssoService.selectConfigById(vo));
    }

    /**
     * 新增参数配置
     */
    @ApiOperation(value = "新增API参数配置")
    @TowerLog(title = "API参数管理", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public Result add(@Validated @RequestBody SysSso vo) {
        // 来自需求1.6.60 来源地址不能超过8182个字符
        if(StringUtils.isNotBlank(vo.getAddress()) && vo.getAddress().length() > 8182) {
            return Result.fail("来源地址超过最大长度限制！");
        }
        vo.setCreateBy(SecurityUtils.getUsername());
        return toAjax(ssoService.insertConfig(vo));
    }

    /**
     * 修改参数配置
     */
    @ApiOperation(value = "修改API参数配置")
    @TowerLog(title = "参数管理", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/update")
    public Result edit(@Validated @RequestBody SysSso vo) {
        // 来自需求1.6.60 来源地址不能超过8182个字符
        if(StringUtils.isNotBlank(vo.getAddress()) && vo.getAddress().length() > 8182) {
            return Result.fail("来源地址超过最大长度限制！");
        }
        vo.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(ssoService.updateConfig(vo));
    }

    /**
     * 删除参数配置
     */
    @ApiOperation(value = "删除API配置")
    @TowerLog(title = "API管理", businessType = BusinessType.DELETE)
    @PostMapping("/del")
    public Result remove(@RequestBody SysSso vo) {
        return toAjax(ssoService.deleteConfigById(vo));
    }




}
