package com.chinatower.platform.project.system.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.project.system.domain.SysPost;
import com.chinatower.platform.project.system.domain.SysRedis;
import com.chinatower.platform.project.system.domain.vo.EditRedisParam;
import com.chinatower.platform.project.system.domain.vo.SysRedisQuery;
import com.chinatower.platform.project.system.service.ISysRedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * video-system
 *
 */
@Api(tags = "缓存管理")
@RestController
@RequestMapping("/system/redis")
public class SysRedisController extends TowerBaseController {

    @Autowired
    private ISysRedisService redisService;

    /**
     * 缓存管理分页查询
     */
    @ApiOperation(value = "分页查询")
    @GetMapping("/page")
    public TableDataInfo page(SysRedisQuery query){
        startPage();
        List<SysRedis> list = redisService.list(query);
        return getDataTable(list);
    }

    /**
     * 缓存管理新增
     */
    @ApiOperation(value = "新增")
    @TowerLog(title = "缓存管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public Result add(@Validated @RequestBody EditRedisParam param){
        return toAjax(redisService.add(param));
    }

    /**
     * 缓存管理查看详情
     */
    @ApiOperation(value = "查看详情")
    @GetMapping("/detail/{id}")
    public AjaxResult getById(@PathVariable Long id){
        return AjaxResult.success(redisService.detail(id));
    }

}
