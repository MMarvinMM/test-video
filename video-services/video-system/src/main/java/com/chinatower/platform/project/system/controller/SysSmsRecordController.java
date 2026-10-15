package com.chinatower.platform.project.system.controller;

import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.project.system.domain.SysSmsRecord;
import com.chinatower.platform.project.system.service.ISysSmsRecord;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * video-system
 *
 */
@Api(tags = "手机验证码记录")
@Slf4j
@RestController
@RequestMapping("/system/sms")
public class SysSmsRecordController extends TowerBaseController {
    @Resource
    private ISysSmsRecord iSysSmsRecord;

    /**
     * 查询手机验证码列表
     */
    @ApiOperation(value = "列表",notes = "列表")
    @GetMapping("/list")
    public TableDataInfo list(SysSmsRecord vo){
        startPage();
        return getDataTable(iSysSmsRecord.list(vo));
    }
}
