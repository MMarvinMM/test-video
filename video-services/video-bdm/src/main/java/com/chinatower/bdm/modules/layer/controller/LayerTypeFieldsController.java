package com.chinatower.bdm.modules.layer.controller;

import com.chinatower.bdm.api.entity.ListByConditionVO;
import com.chinatower.bdm.modules.layer.service.LayerTypeFieldsService;
import com.chinatower.common.core.dto.bdm.AddOrModifyLayerTypeFieldsVO;
import com.chinatower.common.core.dto.bdm.CopyLayerTypeFieldsDTO;
import com.chinatower.common.core.dto.bdm.LayerFieldsDTO;
import com.chinatower.common.core.dto.bdm.LayerFieldsFilterDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * video-bdm
 * @desription: 图层类型字段管理
 * @data 创建时间：2024/1/18 10:32
 * @author chuan hao
 * @version 1.0
 *
 */
@Api(tags = "图层类型字段管理")
@RestController
@RequestMapping("/layerTypeFields")
@RequiredArgsConstructor
public class LayerTypeFieldsController extends TowerBaseController {

    private final LayerTypeFieldsService layerTypeFieldsService;


    @TowerLog(title = "图层类型字段管理", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "新增修改图层类型字段",notes = "新增修改图层类型字段")
    @PostMapping("/addOrModify")
    public Result<Void> addOrModifyFields(@RequestBody AddOrModifyLayerTypeFieldsVO dto){
        return layerTypeFieldsService.addOrModifyFields(dto);
    }

    @TowerLog(title = "图层类型字段管理", businessType = BusinessType.SELECT)
    @ApiOperation(value = "根据 id 删除类型字段",notes = "根据 id 删除类型字段")
    @GetMapping("/deletedByID")
    public Result<Void> deleteByID(String id){
        return layerTypeFieldsService.deleteByID(id);
    }

    @TowerLog(title = "图层类型字段管理", businessType = BusinessType.SELECT)
    @ApiOperation(value = "根据 Abbreviation 删除类型字段",notes = "根据 Abbreviation 删除类型字段")
    @GetMapping("/deletedByAbbreviation")
    public Result<Void> deleteByAbbreviation(String abbreviation){
        return layerTypeFieldsService.deleteByAbbreviation(abbreviation);
    }

    @TowerLog(title = "图层类型字段管理", businessType = BusinessType.SELECT)
    @ApiOperation(value = "查询图层类型字段",notes = "查询图层类型字段")
    @GetMapping("/info")
    public Result<AddOrModifyLayerTypeFieldsVO> info(String id){
        return layerTypeFieldsService.info(id);
    }

    @TowerLog(title = "图层类型字段管理", businessType = BusinessType.SELECT)
    @ApiOperation(value = "查询图层类型字段",notes = "查询图层类型字段")
    @GetMapping("/infos")
    public TableDataInfo infos(ListByConditionVO req){
        startPage();
        return getDataTable(layerTypeFieldsService.infos(req));
    }

    @TowerLog(title = "图层类型字段管理", businessType = BusinessType.SELECT)
    @ApiOperation(value = "查询图层类型字段(字典字段)",notes = "查询图层类型字段(字典字段)")
    @GetMapping("/findFieldsDict")
    public TableDataInfo findFieldsDict(ListByConditionVO req){
        return getDataTable(layerTypeFieldsService.findFieldsDict(req));
    }

    @TowerLog(title = "图层类型字段管理", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "复制图层类型字段",notes = "复制图层类型字段")
    @PostMapping("/copyLayerTypeFields")
    public Result<Void> copyLayerTypeFields(@RequestBody CopyLayerTypeFieldsDTO dto){
        return layerTypeFieldsService.copyLayerTypeFields(dto);
    }

    @TowerLog(title = "图层类型字段管理", businessType = BusinessType.SELECT)
    @ApiOperation(value = "查询图层类型字段",notes = "查询图层类型字段")
    @GetMapping("/layerFields")
    public Result<List<LayerFieldsDTO>> layerFields(String abbreviation){
        return layerTypeFieldsService.layerFields(abbreviation);
    }

    @TowerLog(title = "图层类型字段管理", businessType = BusinessType.SELECT)
    @ApiOperation(value = "查询图层类型字段",notes = "查询图层类型字段")
    @GetMapping("/layerWpFields")
    public Result<List<LayerFieldsDTO>> layerWpFields(String abbreviation){
        return layerTypeFieldsService.layerWpFields(abbreviation);
    }

    @TowerLog(title = "图层类型字段管理", businessType = BusinessType.SELECT)
    @ApiOperation(value = "高级查询图层字段",notes = "高级查询图层字段")
    @GetMapping("/advancedQueryLayerFields")
    public Result<List<LayerFieldsDTO>> advancedQueryLayerFields(String abbreviation){
        return layerTypeFieldsService.advancedQueryLayerFields(abbreviation);
    }


    @TowerLog(title = "图层类型字段管理", businessType = BusinessType.SELECT)
    @ApiOperation(value = "查询图层类型条件筛选",notes = "查询图层类型条件筛选")
    @GetMapping("/layerFieldsFilters")
    public Result<List<LayerFieldsFilterDTO>> layerFieldsFilters(String abbreviation){
        return layerTypeFieldsService.layerFieldsFilters(abbreviation);
    }


}
