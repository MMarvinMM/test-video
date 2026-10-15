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
import com.chinatower.platform.entity.admin.SysDictType;
import com.chinatower.platform.project.system.service.ISysDictTypeService;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * video-system
 * 数据字典信息
 *
 * @author iwhalecloud
 */
@Api(tags = "数据字典信息")
@RestController
@RequestMapping("/system/dict/type")
public class SysDictTypeController extends TowerBaseController
{
    @Autowired
    private ISysDictTypeService dictTypeService;

    /**
     * 查询数据字典
     */
    @ApiOperation(value = "查询数据字典")
    @GetMapping("/list")
    public Result list(SysDictType dictType)
    {
        startPage();
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        return result(list);
    }

//    @ApiOperation(value = "字典类型")
//    @TowerLog(title = "字典类型", businessType = BusinessType.EXPORT)
//    @PostMapping("/export")
//    public void export(@RequestBody  SysDictType dictType, HttpServletResponse response)
//    {
//        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
//        byte[] data =new ExcelUtil<>(SysDictType.class).exportBytes(list);
//        FileUtils.download(response, data);
//    }

    /**
     * 查询字典类型详细
     */
    @ApiOperation(value = "查询字典类型详细")
    @GetMapping(value = "/detail/{dictId}")
    public AjaxResult getInfo(@PathVariable Long dictId)
    {
        return AjaxResult.success(dictTypeService.selectDictTypeById(dictId));
    }

    /**
     * 新增字典类型
     */
    @ApiOperation(value = "新增字典类型")
    @TowerLog(title = "字典类型", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public Result add(@Validated @RequestBody SysDictType dict)
    {
        if (UserConstants.NOT_UNIQUE.equals(dictTypeService.checkDictTypeUnique(dict)))
        {
            return Result.fail("新增字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setCreateBy(SecurityUtils.getUsername());
        return toAjax(dictTypeService.insertDictType(dict));
    }

    /**
     * 修改字典类型
     */
    @ApiOperation(value = "修改字典类型")
    @TowerLog(title = "字典类型", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/update")
    public Result edit(@Validated @RequestBody SysDictType dict)
    {
        if (UserConstants.NOT_UNIQUE.equals(dictTypeService.checkDictTypeUnique(dict)))
        {
            return Result.fail("修改字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(dictTypeService.updateDictType(dict));
    }

    /**
     * 刷新字典类型
     */
    @ApiOperation(value = "刷新字典缓存")
    @TowerLog(title = "字典类型", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/refreshCache/{dictIds}")
    public Result refreshCache(@PathVariable Long[] dictIds) {
        dictTypeService.batchRefreshDictCache(dictIds);
        return Result.success("操作成功");
    }


    /**
     * 删除字典类型
     */
    @ApiOperation(value = "删除字典类型")
    @TowerLog(title = "字典类型", businessType = BusinessType.DELETE)
    @GetMapping("/del/{dictIds}")
    public Result remove(@PathVariable Long[] dictIds)
    {
        return toAjax(dictTypeService.deleteDictTypeByIds(dictIds));
    }

    /**
     * 获取字典选择框列表
     */
    @ApiOperation(value = "获取字典选择框列表")
    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        List<SysDictType> dictTypes = dictTypeService.selectDictTypeAll();
        return AjaxResult.success(dictTypes);
    }
}
