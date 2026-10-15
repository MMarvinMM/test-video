package com.chinatower.bdm.modules.layer.controller;

import com.chinatower.bdm.modules.inspectionRoute.entity.LayerInfoVO;
import com.chinatower.bdm.modules.layer.service.SatelliteLawConfigService;
import com.chinatower.common.core.vo.SatelliteLawConfigVo;
import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.annotation.DataScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * video-bdm
 * 卫片执法配置controller
 * @author cuisj
 * @date 2024-04-24
 * @requestID 卫片执法配置
 */
@RestController
@RequestMapping("/satelliteLawConfig")
@Slf4j
public class SatelliteLawConfigController extends BaseController {

    @Autowired
    private SatelliteLawConfigService satelliteLawConfigService;

    /**
     * 卫片执法配置列表查询
     * @param vo
     * @return
     */
    @PostMapping("/getList")
    @DataScope(dbType ="db1", tenantColumn="s.tenant_id", industryCode="s.industry_code",acceptColumn="s.accept_node")
    public TableDataInfo getList(@RequestBody SatelliteLawConfigVo vo){
        return getDataTable(satelliteLawConfigService.getList(vo));
    }

    /**
     * 卫片执法配置列表查询
     * @param vo
     * @return
     */
    @PostMapping("/getLayerInfo")
    public TableDataInfo getLayerInfo(@RequestBody LayerInfoVO vo){
        return getDataTable(satelliteLawConfigService.getLayerInfo(vo));
    }

    /**
     * 卫片执法配置修改
     * @param vo
     * @return
     */
    @PostMapping("/updateConfig")
    public Result updateConfig(@RequestBody SatelliteLawConfigVo vo){
         satelliteLawConfigService.updateConfig(vo);
         return Result.data("修改成功");
    }

    /**
     * 卫片执法配置逻辑删除
     * @param vo
     * @return
     */
    @PostMapping("/deleteConfig")
    public Result deleteConfig(@RequestBody SatelliteLawConfigVo vo){
        satelliteLawConfigService.deleteConfig(vo);
        return Result.data("删除成功");
    }

    /**
     * 卫片执法配置判断名称是否重复
     * @param vo
     * @return
     */
    @PostMapping("/checkRepeatName")
    @DataScope(dbType ="db1", tenantColumn="s.tenant_id", industryCode="s.industry_code",acceptColumn="s.accept_node")
    public Result checkRepeatName(@RequestBody SatelliteLawConfigVo vo){

        return satelliteLawConfigService.checkRepeatName(vo);
    }
}
