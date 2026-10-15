package com.chinatower.bdm.modules.layer.controller;


import com.alibaba.fastjson.JSON;
import com.chinatower.bdm.modules.layer.domain.LayerTypeStyleVo;
import com.chinatower.bdm.modules.layer.service.LayerTypeFieldsStyleService;
import com.chinatower.bdm.modules.layer.service.LegendService;
import com.chinatower.common.entity.Result;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * video-bdm
 * 图层类型字段样式管理
 */
@Api(tags = "图层类型字段样式管理")
@RestController
@Slf4j
public class LayerTypeStyleController {

    @Resource
    LayerTypeFieldsStyleService layerTypeFieldsStyleService;
    @Resource
    LegendService legendService;

    @ApiOperation(value = "新增修改图层类型样式",notes = "新增修改图层类型样式")
    @PostMapping("/layerTypeStyle/addOrModify")
    public Result<String> addOrModify(@RequestBody LayerTypeStyleVo layerTypeStyle){
        log.info("新增图层类型字段样式管理:{}", JSON.toJSONString(layerTypeStyle));
        layerTypeFieldsStyleService.addOrModify(layerTypeStyle);
        return Result.success("操作成功");
    }

    @ApiOperation(value = "图层类型字段样式查询列表",notes = "图层类型字段样式查询列表")
    @PostMapping("/layerTypeStyle/findByLayerTypeId")
    public Result<LayerTypeStyleVo> listByLayerTypeId(@RequestBody LayerTypeStyleVo listByLayerType){
        LayerTypeStyleVo list = layerTypeFieldsStyleService.listByLayerTypeId(listByLayerType);
        return Result.data(list);
    }

    @ApiOperation(value = "图层类型字段样式清除自定义样式",notes = "图层类型字段样式清除自定义样式")
    @PostMapping("/layerTypeStyle/delStyle")
    public Result<LayerTypeStyleVo> delStyle(@RequestBody LayerTypeStyleVo listByLayerType){
        layerTypeFieldsStyleService.delStyle(listByLayerType);
        return Result.success("清除样式成功");
    }

    @ApiOperation(value = "快捷生成图层样式",notes = "快捷生成图层样式")
    @PostMapping("/layerTypeStyle/quickGenStyles")
    public Result<Void> quickGenStyles(String tenantId, String industryCode, String abbreviation){
        legendService.quickGenStyles(tenantId,industryCode,abbreviation);
        return Result.success("快捷生成图层样式成功");
    }
}
