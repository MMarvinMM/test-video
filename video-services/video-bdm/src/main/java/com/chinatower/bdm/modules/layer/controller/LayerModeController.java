package com.chinatower.bdm.modules.layer.controller;


import com.chinatower.bdm.modules.layer.domain.LayerMode;
import com.chinatower.bdm.modules.layer.service.ILayerModeService;
import com.chinatower.common.core.dto.bdm.LayerTypeDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * video-bdm
 * <p>
 * 图层类型模式管理 前端控制器
 * </p>
 *
 * @author ch
 * @since 2023-04-17
 */
@Api(tags = "图层类型模式管理")
@RestController
@RequestMapping("/layerMode")
@RequiredArgsConstructor
public class LayerModeController {

    private final ILayerModeService layerModeService;
    /**
     *
     * @return 新增修改图层类型
     */
    @ApiOperation(value = "图层模式详情",notes = "图层模式详情")
    @GetMapping("/info")
    public Result<LayerMode> info() {
        return layerModeService.info();
    }

    /**
     *
     * @return 新增修改图层类型
     */
    @TowerLog(title = "图层模式管理", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "新增修改图层模式",notes = "新增修改图层模式")
    @PostMapping("/addOrModify")
    public Result<String> addOrModify(Integer modeValue) {
        return layerModeService.addOrModify(modeValue);
    }

}
