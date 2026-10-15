package com.chinatower.gis.modules.gis.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.entity.GisSdkReq;
import com.chinatower.gis.api.entity.MeasureDistanceBatchVO;
import com.chinatower.gis.modules.gis.service.GisSdkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @desription: 地图组件接口
 * @data 创建时间：2024/10/16 15:18
 * @author chuan hao
 * @version 1.0
 *
 */
@Api(value = "地图组件接口", tags = "地图组件接口")
@Slf4j
@RestController
@RequiredArgsConstructor
public class GisSdkController {

    private final GisSdkService gisSdkService;

    /**
     * 求取多边形范围内栅格值范围
     */
    @ApiOperation(value = "求取多边形范围内栅格值范围")
    @PostMapping("/gis/polygonClipRasterStats")
    public Result polygonClipRasterStats(@RequestBody GisSdkReq req) {
        try {
            return gisSdkService.polygonClipRasterStats(req);
        }catch (Exception e){
            log.error("求取多边形范围内栅格值范围错误",e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 剖面分析内插点
     */
    @ApiOperation(value = "剖面分析内插点")
    @PostMapping("/gis/profileAnalysis")
    public Result profileAnalysis(@RequestBody GisSdkReq req) {
        try {
            return gisSdkService.profileAnalysis(req);
        }catch (Exception e){
            log.error("剖面分析内插点",e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 关键点提取 粗版
     */
    @ApiOperation(value = "关键点提取粗版")
    @PostMapping("/gis/measureDistanceLow")
    public Result measureDistanceLow(@RequestBody GisSdkReq req) {
        try {
            return gisSdkService.measureDistanceLow(req);
        }catch (Exception e){
            log.error("关键点提取粗版",e);
            return Result.fail(e.getMessage());
        }

    }

    /**
     * 关键点提取 高精度
     */
    @ApiOperation(value = "关键点提取高精度")
    @PostMapping("/gis/measureDistanceHigh")
    public Result measureDistanceHigh(@RequestBody GisSdkReq req) {
        try {
            return gisSdkService.measureDistanceHigh(req);
        }catch (Exception e){
            log.error("关键点提取高精度",e);
            return Result.fail(e.getMessage());
        }

    }

    /**
     * 关键点提取 高精度
     */
    @ApiOperation(value = "批量版测距")
    @PostMapping("/gis/measureDistanceBatch")
    public Result measureDistanceBatch(@RequestBody MeasureDistanceBatchVO req) {
        try {
            return gisSdkService.measureDistanceBatch(req);
        }catch (Exception e){
            log.error("关键点提取高精度",e);
            return Result.fail(e.getMessage());
        }

    }



    /**
     * 贴地侧面 粗版
     */
    @ApiOperation(value = "贴地侧面粗版")
    @PostMapping("/gis/measureAreaLow")
    public Result measureAreaLow(@RequestBody GisSdkReq req) {
        try {
            return gisSdkService.measureAreaLow(req);
        }catch (Exception e){
            log.error("贴地侧面粗版",e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 贴地侧面 高精度
     */
    @ApiOperation(value = "贴地侧面高精度")
    @PostMapping("/gis/measureAreaHigh")
    public Result measureAreaHigh(@RequestBody GisSdkReq req) {
        try {
            return gisSdkService.measureAreaHigh(req);
        }catch (Exception e){
            log.error("贴地侧面高精度",e);
            return Result.fail(e.getMessage());
        }
    }

}
