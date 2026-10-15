package com.chinatower.platform.project.system.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.common.utils.ServletUtils;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.ip.IpUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.framework.config.CacheRunner;
import com.chinatower.platform.project.system.service.ISysDictDataService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.resp.SysDictDataResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * video-system
 * 数据字典信息
 *
 * @author iwhalecloud
 */
@Api(tags = "数据字典信息")
@RestController
@Slf4j
public class    SysDictDataController extends TowerBaseController
{
    @Autowired
    private ISysDictDataService dictDataService;

    /**
     * 功能说明:根据条件分页查询字典数据
     *
     * @param dictData 字典数据信息
     * @return TableDataInfo
     * @throws
     */
    @ApiOperation(value = "数据字典列表")
    @GetMapping("/system/dict/data/list")
    public TableDataInfo list(SysDictData dictData)
    {
        startPage();
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        return getDataTable(list);
    }

    /**
     * 字典数据导出
     */
    @ApiOperation(value = "字典数据导出")
    @TowerLog(title = "字典数据", businessType = BusinessType.EXPORT)
    @PostMapping("/system/dict/data/export")
    public void export(@RequestBody  SysDictData dictData, HttpServletResponse response)
    {
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        byte[] data =new ExcelUtil<>(SysDictData.class).exportBytes(list);
        FileUtils.download(response, data);
    }

    /**
     * 查询字典数据详细
     */
    @ApiOperation(value = "查询字典数据详细")
    @GetMapping(value = "/system/dict/data/detail/{dictCode}")
    public Result<SysDictData> getInfo(@PathVariable Long dictCode)
    {
        return Result.data(dictDataService.selectDictDataById(dictCode));
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @ApiOperation(value = "根据字典类型查询字典数据信息")
    @GetMapping(value = "/base/system/dict/data/dictType/{dictType}")
    public Result<List<SysDictData>> dictType(@PathVariable String dictType)
    {
        log.info("访问system服务-dictType,key={},ip={}" , dictType, IpUtils.getIpAddr(ServletUtils.getRequest()));
        return Result.data(dictDataService.selectDictDataByType(dictType));
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @ApiOperation(value = "根据字典类型查询字典数据信息")
    @PostMapping(value = "/base/system/dict/data/dictTypeSdk")
    public Result<List<SysDictData>> dictTypeSdk(@RequestBody String dictType)
    {
        log.info("访问system服务-dictType,key={},ip={}" , dictType, IpUtils.getIpAddr(ServletUtils.getRequest()));
        return Result.data(dictDataService.selectDictDataByType(dictType));
    }

    /**
     * 清楚字典缓存
     */
    @GetMapping(value = "/base/system/dict/remove/dictType/{dictType}")
    public Result<List<SysDictData>> remove(@PathVariable String dictType)
    {
        if("removeAll".equals(dictType)){
            CacheRunner.DICT_DATA_MAP_DICT_TYPE.clear();
        }else if("get".equals(dictType)){

        }else{
            CacheRunner.DICT_DATA_MAP_DICT_TYPE.remove(dictType);
        }
        System.out.println(JSON.toJSON(CacheRunner.DICT_DATA_MAP_DICT_TYPE.keySet()));
        return Result.data(null);
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @ApiOperation(value = "根据字典类型查询字典数据信息")
    @GetMapping(value = "/base/system/dict/data/dictType/multiple/{dictTypes}")
    public Result<Map<String,List<SysDictData>>> dictType(@PathVariable String[] dictTypes){
        log.info("访问system服务-multiple/dictType,key={},ip={}" , dictTypes, IpUtils.getIpAddr(ServletUtils.getRequest()));
        Map<String,List<SysDictData>> resMap = dictDataService.selectDictDataByTypes(dictTypes);
        return Result.data(resMap);
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @ApiOperation(value = "根据字典类型查询字典数据信息")
    @GetMapping(value = "/base/system/dict/data/dictType/multipleSdk")
    public Result<Map<String,List<SysDictData>>> dictTypeSdk(@PathVariable String[] dictTypes){
        log.info("访问system服务-multiple/dictType,key={},ip={}" , dictTypes, IpUtils.getIpAddr(ServletUtils.getRequest()));
        Map<String,List<SysDictData>> resMap = dictDataService.selectDictDataByTypes(dictTypes);
        return Result.data(resMap);
    }

    /**
     * 新增字典类型
     */
    @ApiOperation(value = "新增字典类型")
    @TowerLog(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping(value = "/system/dict/data/add")
    public Result add(@Validated @RequestBody SysDictData dict)
    {
        dict.setCreateBy(SecurityUtils.getUsername());
        return toAjax(dictDataService.insertDictData(dict));
    }

    /**
     * 修改保存字典类型
     */
    @ApiOperation(value = "修改保存字典类型")
    @TowerLog(title = "字典数据", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/system/dict/data/update")
    public Result edit(@Validated @RequestBody SysDictData dict)
    {
        dict.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(dictDataService.updateDictData(dict));
    }

    /**
     * 删除字典类型
     */
    @ApiOperation(value = "删除字典类型")
    @TowerLog(title = "字典类型", businessType = BusinessType.DELETE)
    @GetMapping("/system/dict/data/del/{dictCodes}")
    public Result remove(@PathVariable Long[] dictCodes)
    {
        return toAjax(dictDataService.deleteDictDataByIds(dictCodes));
    }

    /**
     * 根据字典类型查询字典数据信息,对外接口
     */
    @ApiOperation(value = "根据字典类型查询字典数据信息")
    @GetMapping(value = "/base/system/dict/data/dictDataType/{dictType}")
    public Result<List<SysDictDataResponse>> dictDataType(@PathVariable String dictType)
    {
        log.info("访问system服务-dictDataType,key={},ip={}" , dictType, IpUtils.getIpAddr(ServletUtils.getRequest()));
        List<SysDictData> sysDictData = dictDataService.selectDictDataByType(dictType);
        List<SysDictDataResponse> sysDictDataResponses = BeanUtil.copyToList(sysDictData, SysDictDataResponse.class);
        return Result.data(sysDictDataResponses);
    }
}
