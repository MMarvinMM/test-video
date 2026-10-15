package com.chinatower.emergency.controller;

import cn.hutool.core.collection.ListUtil;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.emergency.dto.Digital;
import com.chinatower.emergency.dto.DigitalQueryVO;
import com.chinatower.emergency.dto.DigitalTask;
import com.chinatower.emergency.service.DigitalService;
import com.chinatower.platform.constants.Constants;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import com.chinatower.common.entity.Result;

import java.util.List;

/**
 * video-emergency
 *
 * @author yiyao
 */
@RestController
@Slf4j
@RequestMapping("/digital")
public class DigitalController extends TowerBaseController {

    @Resource
    DigitalService digitalService;


    /**
     * 新增数字预案
     *
     * @return
     */
    @RequestMapping("/save")
    public Result<String> save(@RequestBody Digital digital) {
        digitalService.save(digital);
        return Result.success("新增成功");
    }

    /**
     * 修改数字预案
     *
     * @return
     */
    @RequestMapping("/update")
    public Result<String> update(@RequestBody Digital digital) {
        digitalService.update(digital);
        return Result.success("修改成功");
    }

    /**
     * 查看详情
     *
     * @return
     */
    @RequestMapping("/findById")
    public Result<Digital> findById(@RequestBody Digital digital) {
        Digital res = digitalService.findById(digital);
        return Result.data(res);
    }

    /**
     * 列表查询
     *
     * @return
     */
    @RequestMapping("/list")
    public TableDataInfo list(@RequestBody DigitalQueryVO digital) {
        startPage();
        return getDataTable(digitalService.list(digital));
    }

    /**
     * 数字预案导入应急预案
     *
     * @return
     */
    @PostMapping("/checkImportEmergency")
    public Result<String> checkDigitalImportEmergency(@RequestBody Digital digital) {
        return Result.success(digitalService.checkDigitalImportEmergency(digital));
    }


    /**
     * 数字预案导入应急预案
     *
     * @return
     */
    @PostMapping("/digitalImportEmergency")
    public Result<String> digitalImportEmergency(@RequestBody Digital digital) {
        digitalService.digitalImportEmergency(digital);
        return Result.success("导入完成");
    }


    /**
     * 批量删除
     *
     * @return
     */
    @RequestMapping("/deleteBatch")
    public Result<String> deleteBatch(@RequestBody List<String> ids) {
        digitalService.deleteBatch(ids);
        return Result.success("删除成功");
    }

    @RequestMapping("/findStage")
    public TableDataInfo findStage(@RequestBody DigitalQueryVO digital) {
        return getDataTable(digitalService.findStage(digital));
    }

    @RequestMapping("/findTasksByStage")
    public TableDataInfo findTasksByStage(@RequestBody DigitalQueryVO digital) {
        if (digital.getPageNum() == null || digital.getPageSize() == null) {
            digital.setPageNum(1);
            digital.setPageSize(5);
        }
        List<DigitalTask> tasksByStage = digitalService.findTasksByStage(digital);

        int pageNumNew = pageHandle(tasksByStage.size(), digital.getPageSize(), digital.getPageNum());
        List<DigitalTask> result = ListUtil.page(pageNumNew - 1, digital.getPageSize(), tasksByStage);
        //手动分页
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setRows(result);
        rspData.setTotal(tasksByStage.size());
        return rspData;
    }


    private int pageHandle(int total, int pageSize, int pageNum) {
        int pageNumNew;
        int pages;
        if (pageSize > Constants.ZERO_NUM) {
            pages = total / pageSize + ((total % pageSize == Constants.ZERO_NUM) ? Constants.ZERO_NUM : Constants.ONE_NUM);
        } else {
            pages = Constants.ZERO_NUM;
        }
        if (pageNum > pages) {
            pageNum = pages;
        }
        pageNumNew = pageNum;
        return pageNumNew;
    }

}
