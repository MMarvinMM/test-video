package com.chinatower.bdm.modules.layer.controller;


import com.chinatower.bdm.modules.layer.domain.LayerPolygon;
import com.chinatower.bdm.modules.layer.service.ILayerPolygonService;
import com.chinatower.common.core.dto.bdm.*;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.utils.BeanCopyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * video-bdm
 * <p>
 * 图层图斑管理 前端控制器
 * </p>
 *
 * @author ch
 * @since 2023-04-10
 */
@Api(tags = "图层图斑管理")
@RestController
@RequestMapping("/layerPolygon")
@RequiredArgsConstructor
public class LayerPolygonController extends TowerBaseController {

    private final ILayerPolygonService layerPolygonService;


    /**
     * @return 新增修改图层类型
     */
    @ApiOperation(value = "图斑列表", notes = "图斑列表")
    @GetMapping("/list")
    public TableDataInfo list(LayerPolygonQueryDTO dto) {
        startPage();
        return getDataTable(layerPolygonService.list(dto));
    }

    /**
     * @return 新增修改图层类型
     */
    @ApiOperation(value = "图层下图斑数量", notes = "图层下图斑数量")
    @GetMapping("/getLayerPolygonCount")
    public Result<Integer> getLayerPolygonCount(String layerId) {
        return Result.data(layerPolygonService.getLayerPolygonCount(layerId));
    }

    /**
     * @return 新增修改图层类型
     */
    @ApiOperation(value = "图斑列表", notes = "图斑列表")
    @GetMapping("/getTenantList")
    public Result<List<LayerPolygonDTO>> getTenantList(LayerPolygonQueryDTO dto) {
        List<LayerPolygon> list = layerPolygonService.list(dto);
        List<LayerPolygonDTO> dtos = BeanCopyUtils.copyList(list, LayerPolygonDTO.class);
        return Result.data(dtos);
    }

    /**
     * @param layerId   图层ID
     * @param featureId 图斑ID
     * @return 图斑详情
     */
    @ApiOperation(value = "图斑列表", notes = "图斑列表")
    @GetMapping("/info")
    public Result<LayerPolygonDTO> info(String layerId, String featureId) {
        LayerPolygonDTO dto = layerPolygonService.info(layerId, featureId);
        return Result.data(dto);
    }

    /**
     * @param dto
     * @return 新增修改图层类型
     */
    @TowerLog(title = "图斑管理", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "新增修改图斑", notes = "新增修改图斑")
    @PostMapping("/add")
    public Result<String> addLayerPolygon(@Validated @RequestBody LayerPolygonAddDTO dto) {
        return layerPolygonService.addLayerPolygon(dto);
    }
}
