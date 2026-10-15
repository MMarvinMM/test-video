package com.chinatower.platform.project.system.controller;

import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;

import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.project.system.domain.SysFileInfo;
import com.chinatower.platform.project.system.service.ISysFileInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * video-system
 * 文件信息Controller
 *
 * @author wangzic
 * @date 2020-04-03
 */
@Api(tags = "文件信息Controller")
@RestController
public class SysFileInfoController extends TowerBaseController
{
    @Autowired
    private ISysFileInfoService sysFileInfoService;

    /**
     * 查询文件信息列表
     */
//    @ApiOperation(value = "查询文件信息列表")
//    @GetMapping("/system/fileinfo/list")
//    public TableDataInfo list(SysFileInfo sysFileInfo)
//    {
//        startPage();
//        List<SysFileInfo> list = sysFileInfoService.selectSysFileInfoList(sysFileInfo);
//        return getDataTable(list);
//    }

//    /**
//     * 导出文件信息列表
//     */
//    @ApiOperation(value = "导出文件信息列表")
//    @TowerLog(title = "文件信息", businessType = BusinessType.EXPORT)
//    @PostMapping("/system/fileinfo/export")
//    public void export(@RequestBody  SysFileInfo sysFileInfo, HttpServletResponse response)
//    {
//        //如果排序不为空
//        if (StringUtils.isNotEmpty(sysFileInfo.getOrderByColumn())){
//            //且排序不符合校验,置空过滤
//            if (!SqlUtil.isValidOrderBySql(sysFileInfo.getOrderByColumn())) {
//                sysFileInfo.setOrderByColumn(null);
//            }
//        }
//        List<SysFileInfo> list = sysFileInfoService.selectSysFileInfoList(sysFileInfo);
//        byte[] data =new ExcelUtil<>(SysFileInfo.class).exportBytes(list);
//        FileUtils.download(response, data);
//    }

    /**
     * 获取文件信息详细信息
     */
//    @ApiOperation(value = "获取文件信息详细信息")
//    @GetMapping(value = "/system/fileinfo/detail/{fileId}")
//    public AjaxResult getInfo(@PathVariable("fileId") String fileId)
//    {
//        return AjaxResult.success(sysFileInfoService.selectSysFileInfoById(fileId));
//    }

    /**
     * 批量查询文件信息
     */
//    @ApiOperation(value = "批量查询文件信息")
//    @GetMapping("/base/system/fileinfo/byIds/{fileIds}")
//    public AjaxResult getInfos(@PathVariable String[] fileIds)
//    {
//        return AjaxResult.success(sysFileInfoService.selectSysFileInfoByIds(fileIds));
//    }

//    /**
//     * 新增文件信息
//     */
//    @ApiOperation(value = "新增文件信息")
//    @TowerLog(title = "文件信息", businessType = BusinessType.INSERT)
//    @PostMapping(value = "/system/fileinfo/add")
//    public Result add(@RequestBody SysFileInfo sysFileInfo)
//    {
//        return toAjax(sysFileInfoService.insertSysFileInfo(sysFileInfo));
//    }

//    /**
//     * 修改文件信息
//     */
//    @ApiOperation(value = "修改文件信息")
//    @TowerLog(title = "文件信息", businessType = BusinessType.UPDATE)
//    @PostMapping(value = "/system/fileinfo/update")
//    public Result edit(@RequestBody SysFileInfo sysFileInfo)
//    {
//        return toAjax(sysFileInfoService.updateSysFileInfo(sysFileInfo));
//    }
//
//    /**
//     * 删除文件信息
//     */
//    @ApiOperation(value = "删除文件信息")
//    @TowerLog(title = "文件信息", businessType = BusinessType.DELETE)
//	@GetMapping("/system/fileinfo/del/{fileIds}")
//    public Result remove(@PathVariable String[] fileIds)
//    {
//        return toAjax(sysFileInfoService.deleteSysFileInfoByIds(fileIds));
//    }
}
