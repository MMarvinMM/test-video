package com.chinatower.bdm.modules.layer.controller;


import com.chinatower.bdm.api.LayerApi;
import com.chinatower.bdm.api.entity.ListByConditionVO;
import com.chinatower.bdm.modules.layer.service.ILayerTypeService;
import com.chinatower.common.core.domain.R;
import com.chinatower.common.core.dto.bdm.*;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
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
 * <p>
 * 图层类别管理 前端控制器
 * </p>
 *
 * @author ch
 * @since 2023-04-06
 */
@Api(tags = "图层类别管理")
@RestController
@RequestMapping("/layerType")
@RequiredArgsConstructor
public class LayerTypeController extends TowerBaseController {

    private final ILayerTypeService layerTypeService;
    /**
     *
     * @return 新增修改图层类型
     */
    @ApiOperation(value = "图层类别列表",notes = "图层类别列表")
    @GetMapping("/list")
    public Result<List<LayerTypeDTO>> list(String typeName) {
        return layerTypeService.list(typeName);
    }

    /**
     *
     * @return 创建“其他类型”数据类型的表信息
     */
    @ApiOperation(value = "创建 其他类型 数据类型的表信息",notes = "创建 其他类型 数据类型的表信息")
    @GetMapping("/createQtTypeInfo")
    public Result<String> createQtTypeInfo() {
        return layerTypeService.createQtTypeInfo();
    }

    /**
     *
     * @return 根据条件查询类型列表
     */
    @ApiOperation(value = "图层类别列表",notes = "图层类别列表")
    @GetMapping("/listByCondition")
    public TableDataInfo listByCondition(ListByConditionVO req) {
        startPage();
        return getDataTable(layerTypeService.listByCondition(req));
    }



    /**
     *
     * @param vos
     * @return 新增修改图层类型
     */
    @TowerLog(title = "图层类型管理", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "新增修改图层类型",notes = "新增修改图层类型")
    @PostMapping("/addOrModify")
    public Result<String> addLayerTypes(@RequestBody AddLayerTypeDTO vos) {
        if (vos.getIds() != null && !vos.getIds().isEmpty()){
            Result<String> result = layerTypeService.deleted(vos.getIds());
            if (result.getCode() != ResultCode.SUCCESS.getCode()){
                return result;
            }
        }
        if (vos.getVos() == null || vos.getVos().isEmpty()){
            return Result.success("图层类型配置成功");
        }
        return layerTypeService.addLayerTypes(vos.getVos());
    }

    /**
     *
     * @return 新增修改图层类型
     */
    @TowerLog(title = "图层类型管理", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "删除图层类型",notes = "删除图层类型")
    @PostMapping("/deleteCheck")
    public Result<String> deleteCheck(@RequestBody List<String> ids) {
        if (ids == null || ids.isEmpty()){
            return Result.success("参数错误");
        }
        return layerTypeService.deleteCheck(ids);
    }

    @TowerLog(title = "图层类型管理", businessType = BusinessType.SELECT)
    @ApiOperation(value = "图层类型名称重复校验",notes = "图层类型名称重复校验")
    @PostMapping("/layerTypeNamesRepeat")
    public Result<String> layerTypeNamesRepeat(@RequestBody List<LayerTypeNamesRepeatVO> vos) {
        return layerTypeService.layerTypeNamesRepeat(vos);
    }

    /**
     *
     * @param vo
     * @return 新增修改图层类型
     */
    @TowerLog(title = "图层类型管理", businessType = BusinessType.DELETE)
    @ApiOperation(value = "校验图层类型是否可以删除",notes = "校验图层类型是否可以删除")
    @PostMapping("/validateLayerType")
    public Result<String> validateLayerType(@RequestBody DeleteDTO vo) {
        return layerTypeService.validateLayerType(vo.getIds());
    }

    /**
     *
     * @param vo
     * @return 新增修改图层类型
     */
    @TowerLog(title = "图层类型管理", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除图层类型",notes = "删除图层类型")
    @PostMapping("/deleted")
    public Result<String> deleted(@RequestBody DeleteDTO vo) {
        return layerTypeService.deleted(vo.getIds());
    }

    /**
     *
     * @return 图层类型复制
     */
    @TowerLog(title = "图层类型管理", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "图层类型复制",notes = "图层类型复制")
    @GetMapping("/copy")
    public Result<String> copy(String layerTypeId) {
        return layerTypeService.copyAddData(layerTypeId);
    }

    /**
     *
     * @return 图层类型复制
     */
    @TowerLog(title = "图层类型管理", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "图层类型复制数据返回",notes = "图层类型复制数据返回")
    @GetMapping("/copyGetData")
    public Result<LayerTypeDTO> copyGetData(String layerTypeId) {
        return layerTypeService.copyGetData(layerTypeId);
    }

    /**
     *
     * @return 新增图层类型
     */
    @TowerLog(title = "图层类型管理", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "新增类型",notes = "新增类型")
    @PostMapping("/addLayerType")
    public Result<String> addLayerType(@RequestBody SysLayerTypeDTO dto) {
        return layerTypeService.addLayerType(dto);
    }

    /**
     *
     * @return 修改图层类型
     */
    @TowerLog(title = "图层类型管理", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改图层类型",notes = "修改图层类型")
    @PostMapping("/modifyLayerType")
    public Result<String> modifyLayerType(@RequestBody SysLayerTypeDTO dto) {
        return layerTypeService.modifyLayerType(dto);
    }

    /**
     *
     * @return 根据简称获取所有字典  selectByAbbreviation LayerTypeAttrFilterVO
     */
    @TowerLog(title = "图层类型管理", businessType = BusinessType.SELECT)
    @ApiOperation(value = "根据简称获取所有字典",notes = "根据简称获取所有字典")
    @GetMapping("/dictByAbbreviation")
    public Result<List<LayerTypeAttrFilterVO>> dictByAbbreviation(String abbreviation) {
        return Result.data(layerTypeService.dictByAbbreviation(abbreviation));
    }

}
