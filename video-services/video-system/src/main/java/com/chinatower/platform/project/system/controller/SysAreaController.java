package com.chinatower.platform.project.system.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.entity.admin.SysTiledArea;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.project.system.domain.vo.SysDeptDeviceTreeVO;
import com.chinatower.platform.project.system.service.ISysAreaService;
import com.chinatower.system.api.model.StreetTreeVO;
import com.chinatower.system.api.model.SysArea;
import com.chinatower.system.api.model.SysCapitalVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * video-system
 * 行政区域Controller
 *
 * @author wangzic
 * @date 2019-12-17
 */
@Api(tags = "行政区域Controller")
@RestController
@Slf4j
public class SysAreaController extends TowerBaseController implements CommandLineRunner {

    @Autowired
    private ISysAreaService sysAreaService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询行政区域列表
     */
    @ApiOperation(value = "查询行政区域列表")
    @GetMapping("/system/area/list")
    public TableDataInfo list(SysArea sysArea) {
        startPage();
        List<SysArea> list = sysAreaService.selectSysAreaList(sysArea);
        return getDataTable(list);
    }

    /**
     * 获取部门下拉树列表
     */
    @ApiOperation(value = "获取部门下拉树列表")
    @GetMapping("/base/system/area/treeselect")
    public AjaxResult treeselect() {
        return AjaxResult.success(new ArrayList<TreeSelect>());
    }

    /**
     * 导出行政区域列表
     */
    @ApiOperation(value = "导出行政区域列表")
    @TowerLog(title = "行政区域", businessType = BusinessType.EXPORT)
    @PostMapping("/system/area/export")
    public void export(@RequestBody SysArea sysArea, HttpServletResponse response) {
        List<SysArea> list = sysAreaService.selectSysAreaList(sysArea);
        byte[] data = new ExcelUtil<>(SysArea.class).exportBytes(list);
        FileUtils.download(response, data);
    }

    /**
     * 获取行政区域详细信息
     */
    @ApiOperation(value = "获取行政区域详细信息")
    @GetMapping(value = "/system/area/detail/{areaCode}")
    public AjaxResult getInfo(@PathVariable("areaCode") String areaCode) {
        return AjaxResult.success(sysAreaService.selectSysAreaById(areaCode));
    }

    /**
     * 获取行政区域详细信息
     */
    @ApiOperation(value = "获取行政区域详细信息")
    @GetMapping(value = "/system/areas")
    public Result<Map<String,SysArea>> getInfos(@RequestParam String [] codes) {
        return Result.data(sysAreaService.getInfos(codes));
    }

    /**
     * 获取行政区域详细信息
     */
    @ApiOperation(value = "获取行政区域详细信息")
    @GetMapping(value = "/system/area/getAreaInfo/{areaCode}")
    public Result getAreaInfo(@PathVariable String areaCode) {

        SysArea sysArea = sysAreaService.selectSysAreaById(areaCode);
        return Result.data(sysArea);
    }

    /**
     * 新增行政区域
     */
    @ApiOperation(value = "新增行政区域")
    @TowerLog(title = "行政区域", businessType = BusinessType.INSERT)
    @PostMapping(value = "/system/area/add")
    public Result add(@RequestBody SysArea sysArea) {
        Result result = toAjax(sysAreaService.insertSysArea(sysArea));
        if (result != null && result.getCode() == ResultCode.SUCCESS.getCode()) {
            removeCaffeineCache();
        }
        return result;
    }

    /**
     * 修改行政区域
     */
    @ApiOperation(value = "修改行政区域")
    @TowerLog(title = "行政区域", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/system/area/update")
    public Result edit(@RequestBody SysArea sysArea) {
        Result result = toAjax(sysAreaService.updateSysArea(sysArea));
        if (result != null && result.getCode() == ResultCode.SUCCESS.getCode()) {
            removeCaffeineCache();
        }
        return result;
    }

    /**
     * 删除行政区域
     */
    @ApiOperation(value = "删除行政区域")
    @TowerLog(title = "行政区域", businessType = BusinessType.DELETE)
    @GetMapping("/system/area/del/{areaCodes}")
    public Result remove(@PathVariable String[] areaCodes) {
        Result result = toAjax(sysAreaService.deleteSysAreaByIds(areaCodes));
        if (result != null && result.getCode() == ResultCode.SUCCESS.getCode()) {
            removeCaffeineCache();
        }
        return result;
    }

    /**
     * 查询所有行政区域
     */
    @ApiOperation(value = "查询所有行政区域")
    @GetMapping("/system/area/queryAllAreaCode")
    public Result<List<SysArea>> queryAllAreaCode() {
        List<SysArea> list = sysAreaService.selectAll();
        return Result.data(list);
    }

    private Map<String,List<SysArea>> treeManagement(List<SysArea> list){
        Map<String,List<SysArea>> result = new HashMap<>();
        if(null == list){
            return result;
        }
        List<SysArea> provinceList = new ArrayList<>();
        List<SysArea> cityList = new ArrayList<>();
        List<SysArea> areaList = new ArrayList<>();
        for(SysArea a:list){
            switch (a.getAreaLevel()){
                case "2":
                    provinceList.add(a);
                    break;
                case "3":
                    cityList.add(a);
                    break;
                case "4":
                    areaList.add(a);
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    /**
     * 根据租户查询管理区域
     */
    @ApiOperation(value = "根据租户查询管理区域")
    @GetMapping("/system/area/queryManagementAreaTreeByTenant")
    public Result queryManagementAreaTreeByTenant(@RequestParam("tenantId") String tenantId){
        List<SysDeptDeviceTreeVO> list = sysAreaService.getManagementAreaTreeByTenantId(tenantId);
        return Result.data(list);
    }

    /**
     * 查询平铺行政区域
     */
    @ApiOperation(value = "查询平铺行政区域")
    @PostMapping("/system/area/tiled/query")
    public Result<List<SysTiledArea>> querySysTiledArea(@RequestBody Map<String, Object> params) {
        return Result.data(sysAreaService.querySysTiledArea(params));
    }

    /**
     * 获取行政区划信息
     *
     * @param sysArea 区划信息
     * @return Result 处理结果
     */
    @PostMapping("/video/getRegion")
    public Result<List<SysArea>> getRegion(@RequestBody SysArea sysArea) {
        sysArea.setPageNum(null);
        sysArea.setPageSize(null);
        Result result = null;
        if (StringUtils.isEmpty(sysArea.getAreaLevel())) {
            return Result.fail("行政区划等级不能为空");
        }
        List<SysArea> list = sysAreaService.getArea(sysArea);
        result = Result.success("查询成功");
        result.setData(list);
        return result;
    }
    /**
     * 查询省会
     *
     * @param vo
     * @return
     */
    @PostMapping("/system/area/getSysCapital")
    public Result<SysCapitalVO> getSysCapital(@RequestBody SysCapitalVO vo){
        SysCapitalVO sysCapitalVO = sysAreaService.getSysCapital(vo);
        return Result.data(sysCapitalVO);
    }


    /**
     * 获取街道列表
     */
    @ApiOperation(value = "获取街道列表")
    @PostMapping(value = "/system/area/getAllStreetList")
    public Result<List<StreetTreeVO>> getAllStreetList(@RequestBody StreetTreeVO vo) {
        Result result = null;
        List<StreetTreeVO> list = sysAreaService.getAllStreetList(vo);
        result = Result.success("查询成功");
        result.setData(list);
        return result;
    }

    /**
     * 获取部门下拉树列表
     */
    @ApiOperation(value = "获取部门下拉树列表")
    @PostMapping("/base/system/area/treeselectNew")
    public Result<List<TreeSelect>>  treeselectNew() {
        Result result = null;
        List<TreeSelect> list =sysAreaService.selectTreeSelect();
        result = Result.success("查询成功");
        result.setData(list);
        return result;
    }
    /**
     * 获取部门下拉树列表
     * @Description: 获取部门下拉树列表（只取省市区3级）
     * @return com.chinatower.common.entity.Result<java.util.List<com.chinatower.common.entity.TreeSelect>>
     * @Author: TangJinBo
     * @Date: 2022/12/19 16:43
     */
    @ApiOperation(value = "获取部门下拉树列表")
    @PostMapping("/base/system/area/treeArea")
    public Result<List<TreeSelect>> treeArea() {
        return Result.data(sysAreaService.treeArea());
    }

    /**
     * 查询全部省市区
     */
    @PostMapping("/system/area/getAllArea")
    public Result<List<SysArea>> getAllArea(@RequestBody SysArea sysArea) {
        Result result = null;
        List<SysArea> list = sysAreaService.getAllArea(sysArea);
        result = Result.success("查询成功");
        result.setData(list);
        return result;
    }

    /**
     * 根据区划编码获取经纬度
     */
    @PostMapping("/system/area/getCenterLonLat")
    public Result<SysArea> getCenterLonLat(@RequestParam("code")String code){
        Result<SysArea> result;
        SysArea sysArea = sysAreaService.getCenterLonLat(code);
        result = Result.success("查询成功");
        result.setData(sysArea);
        return result;
    }

    /**
     * 根据区划名称查询信息
     * @Description: 根据区划名称查询信息
     * @param areaNames 入参
     * @return com.chinatower.common.entity.Result<java.util.List<system.domain.SysArea>>
     * @Author: TangJinBo
     * @Date: 2022/12/14 11:34
     */
    @PostMapping("/system/area/selectByAreaName")
    public Result<List<SysArea>> selectByAreaName(@RequestBody List<String> areaNames){
        return Result.data(sysAreaService.selectByAreaName(areaNames));
    }

    /**
     * 获取行政区域详细信息
     */
    @PostMapping(value = "/system/area/getAreaCaffeineMap")
    public Result getAreaCaffeineMap(@RequestBody(required = false) List<String> areaCodes) {
        return Result.data(sysAreaService.selectAreaMap(areaCodes));
    }

    /**
     * 获取行政区域名称
     */
    @PostMapping(value = "/system/area/getAreaNameCaffeineMap")
    public Result getAreaNameCaffeineMap(@RequestBody(required = false) List<String> areaCodes) {
        return Result.data(sysAreaService.selectAreaWithNameRel(areaCodes));
    }

    @PostMapping(value = "/system/area/removeCaffeineCache")
    public void removeCaffeineCache(){
        try {
            redisTemplate.convertAndSend("removeCaffeineCache","remove");
        } catch (Exception e) {
            log.error("removeCaffeineCache error", e);
        }
    }

    public void testsub(){
        new Thread(()->{
            System.out.println("test......");
            redisTemplate.execute((RedisCallback) connection -> {
                connection.subscribe((message, pattern) -> {
                    System.out.println("收到消息，使用redisTemplate收到的：" + message);
                    sysAreaService.removeCaffeineCache();
                }, "removeCaffeineCache".getBytes());
                return null;
            });
        }).start();
    }

    @Override
    public void run(String... args) throws Exception {
        testsub();
    }

    /**
     * 获取行政区域列表
     */
    @GetMapping("/system/area/getAreaTreeList")
    public Result<List<SysArea>> getAreaTreeList(){
        List<SysArea> sysArea = sysAreaService.getAreaTreeList();
        return Result.data(sysArea);
    }

    /**
     * 查询全国省市区乡镇的数据
     * @return
     */
    @GetMapping("/system/area/getAreaTreeWithTownList")
    public Result<List<TreeSelect>> getAreaTreeWithTownList(@RequestParam("otherFlag") boolean otherFlag){
        List<TreeSelect> treeSelect = sysAreaService.getAreaTreeWithTownList(otherFlag);
        return Result.data(treeSelect);
    }
}
