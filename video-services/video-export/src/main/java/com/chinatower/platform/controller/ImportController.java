package com.chinatower.platform.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.platform.common.constant.Constants;
import com.chinatower.platform.common.constant.HttpStatus;
import com.chinatower.platform.common.utils.ServletUtils;
import com.chinatower.platform.entity.SysImportDetail;
import com.chinatower.platform.entity.SysImportRecord;
import com.chinatower.platform.service.ISysImportService;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 * video-export
 * 导入记录管理
 *
 * @author wangzic
 * @date 2020-09-07
 */
@Api(tags = "导入记录管理")
@RestController
@RequestMapping("/base/imported")
public class ImportController {

    @Autowired
    private ISysImportService ISysImportService;

    /**
     * 查询导入记录明细列表
     * @param detail
     * @return
     */
    @ApiOperation("查询导入记录明细列表")
    @GetMapping("/detail/list")
    public TableDataInfo list(SysImportDetail detail) {
        PageMethod.startPage(ServletUtils.getParameterToInt(Constants.PAGE_NUM,1),
                ServletUtils.getParameterToInt(Constants.PAGE_SIZE,10));
        List<SysImportDetail> list = ISysImportService.selectImportDetailList(detail);
        TableDataInfo tableDataInfo = new TableDataInfo(list, (int) new PageInfo<>(list).getTotal());
        tableDataInfo.setCode(HttpStatus.SUCCESS);
        return tableDataInfo;
    }


    /**
     * 查询导入记录列表
     * @param sysImportRecord
     * @return
     */
    @ApiOperation("查询导入记录列表")
    @GetMapping("/record/list")
    public TableDataInfo list(SysImportRecord sysImportRecord) {
        PageMethod.startPage(ServletUtils.getParameterToInt(Constants.PAGE_NUM,1),
                ServletUtils.getParameterToInt(Constants.PAGE_SIZE,10));
        List<SysImportRecord> list = ISysImportService.selectImportRecordList(sysImportRecord);
        TableDataInfo tableDataInfo = new TableDataInfo(list, (int) new PageInfo<>(list).getTotal());
        tableDataInfo.setCode(HttpStatus.SUCCESS);
        return tableDataInfo;
    }

    /**
     * 刷新导入记录信息
     * @param importId
     * @return
     */
    @ApiOperation("刷新导入记录信息")
    @GetMapping(value = "/record/latest/{importId}")
    public Result getLatestImportInfoById(@PathVariable("importId") String importId) {
        return Result.data(ISysImportService.selectLatestRecordById(importId));
    }

}
