package com.chinatower.platform.project.system.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.project.system.service.ISysDeptStreetRelService;
import com.chinatower.system.api.model.SysDeptStreetRelVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * video-system
 *
 */
@Api(tags = "组织和行政区域关联关系")
@Slf4j
@RestController
@RequestMapping("/system/sysDeptStreetRel")
public class SysDeptStreetRelController extends TowerBaseController {
    @Resource
    private ISysDeptStreetRelService iSysDeptStreetRelService;

    /**
     * 组织和行政区域关联关系查询
     */
    @ApiOperation(value = "组织和行政区域关联关系查询",notes = "组织和行政区域关联关系查询")
    @PostMapping("/getDeptStreetRel")
    public Result<List<SysDeptStreetRelVO>> selectDeptStreetRelList(@RequestBody SysDeptStreetRelVO vo){

        return iSysDeptStreetRelService.selectDeptStreetRelList(vo);
    }

    /**
     * 新增组织和行政区域关联关系
     */
    @ApiOperation(value = "新增组织和行政区域关联关系",notes = "新增组织和行政区域关联关系")
    @PostMapping("/insertDeptStreetRel")
    public Result insertDeptStreetRel(@RequestBody List<SysDeptStreetRelVO> list){
        Result res = iSysDeptStreetRelService.insertDeptStreetRel(list);
        return res;
    }

    /**
     * 删除组织和行政区域关联关系
     */
    @ApiOperation(value = "删除组织和行政区域关联关系")
    @PostMapping("/deleteDeptStreetRel")
    public Result deleteDeptStreetRel(@RequestBody SysDeptStreetRelVO vo) {
        return iSysDeptStreetRelService.deleteDeptStreetRel(vo);
    }



}
