package com.chinatower.bdm.modules.layer.controller;

import com.chinatower.bdm.modules.layer.service.SatelliteFilmLayerService;
import com.chinatower.common.core.dto.bdm.LayerAddDTO;
import com.chinatower.common.core.dto.bdm.LayerIdVo;
import com.chinatower.common.core.dto.bdm.SatelliteFilmLayerUploadVo;
import com.chinatower.common.entity.Result;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * video-bdm
 * @desription: 卫片相关图层
 * @data 创建时间：2024/5/6 14:28
 * @author chuan hao
 * @version 1.0
 *
 */
@Api(tags = "图层类型字段管理")
@RestController
@RequestMapping("/satelliteFilmLayer")
@RequiredArgsConstructor
public class SatelliteFilmLayerController {

    private final SatelliteFilmLayerService satelliteFilmLayerService;

    /**
     * @return 新增修改图层类型
     */
    @TowerLog(title = "图层管理", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "新增修改图层", notes = "新增修改图层")
    @PostMapping("/addOrModify")
    public Result<Void> addLayer(@Validated @RequestBody SatelliteFilmLayerUploadVo dto) {
        satelliteFilmLayerService.satelliteFilmLayerUpload(dto);
        return Result.success("上传完成");
    }

    /**
     * @return 新增修改图层类型
     */
    @TowerLog(title = "图层管理", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "生成告警", notes = "生成告警")
    @PostMapping("/createAlarm")
    public Result<Void> createAlarm(@RequestBody LayerIdVo vo) {
        satelliteFilmLayerService.createAlarm(vo);
        return Result.success("成功");
    }
}
