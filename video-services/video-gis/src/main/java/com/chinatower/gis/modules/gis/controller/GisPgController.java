package com.chinatower.gis.modules.gis.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.dto.bdm.LayerClassifyAreaStatisticsVO;
import com.chinatower.common.core.dto.bdm.LayerSlzyStatisticsDTO;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.entity.CamaraDeviceParamsInfoVO;
import com.chinatower.gis.api.GisPgAPI;
import com.chinatower.gis.api.entity.AltitudesReq;
import com.chinatower.gis.api.entity.AltitudesRes;
import com.chinatower.gis.api.entity.TableNameListReq;
import com.chinatower.event.entity.vo.CameraCoordRes;
import com.chinatower.gis.api.entity.gis.GisFireCombustibleRes;
import com.chinatower.gis.api.entity.gis.GisFireGradeRes;
import com.chinatower.gis.api.entity.gis.GisSlopeAspectRes;
import com.chinatower.gis.api.entity.gis.GisVegetationDataRes;
import com.chinatower.gis.api.entity.layer.*;
import com.chinatower.gis.modules.gis.service.GisPgService;
import com.chinatower.platform.common.exception.CustomException;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(value = "gis-pg SDK", tags = "gis-pg SDK")
@Slf4j
@RestController
@RequiredArgsConstructor
public class GisPgController implements GisPgAPI {

    private final GisPgService gisPgService;
    /**
     * 海拔高度
     * @param lon 经度
     * @param lat 维度
     */
    @Override
    @GetMapping("/pg/altitudeHeightQuery")
    public Result<Integer> altitudeHeightQuery(String lon, String lat) {
        if (StringUtils.isEmpty(lon) || StringUtils.isEmpty(lat)){
            return Result.fail("经纬度不能为空");
        }
        Integer h = gisPgService.altitudeHeightQuery(lon, lat);
        return Result.data(h);
    }
    /**
     * 海拔高度
     */
    @Override
    @PostMapping("/pg/altitudeHeightQuerys")
    public Result<List<AltitudesRes>> altitudeHeightQuerys(@RequestBody  AltitudesReq req) {
        if (req == null || req.getLonLat().isEmpty()){
            return Result.fail("经纬度不能为空");
        }
        return gisPgService.altitudeHeightQuerys(req);
    }

    /**
     * 查询图层数据
     */
    @Override
    @PostMapping("/pg/tableNameList")
    public Result<List<Map<String,Object>>> tableNameList(@RequestBody TableNameListReq req) {
        return Result.data(gisPgService.tableNameList(req));
    }

    /**
     *  提供视频视图联动功能实时视频画面中增加测高度
     */
    @Override
    @PostMapping("/pg/getCameraCoord")
    public Result<CameraCoordRes> getCameraCoord(@RequestBody CamaraDeviceParamsInfoVO camaraDeviceParamsInfoVO) {
        log.info("get camera coord 提供视频视图联动功能实时视频画面中增加测高度：{}", JSON.toJSONString(camaraDeviceParamsInfoVO));
        return Result.data(gisPgService.getCameraCoord(camaraDeviceParamsInfoVO));
    }

    /**
     * 查询统计面积
     */
    @Override
    @PostMapping("/pg/layerClassifyAreaStatistics")
    public Result<List<LayerSlzyStatisticsDTO>> layerClassifyAreaStatistics(LayerClassifyAreaStatisticsVO vo) {
        log.info("查询统计面积：{}", JSON.toJSONString(vo));
        try {
            return Result.data(gisPgService.layerClassifyAreaStatistics(vo));
        }catch (CustomException e){
            throw e;
        }catch (Exception e){
            log.info("分类面积统计失败",e);
        }
        throw new CustomException("分类面积统计失败");
    }

    /**
     * 耕地类别
     */
    @Override
    public Result<List<LayerXztbDlbmAreaRes>> layerXztbDlbmAreaRes(LayerYearTableName vo) {
        try {
            return Result.data(gisPgService.layerXztbDlbmAreaRes(vo));
        }catch (CustomException e){
            throw e;
        }catch (Exception e){
            log.info("耕地类型统计失败",e);
        }
        throw new CustomException("耕地类型统计失败");
    }

    /**
     * 耕地等别
     */
    @Override
    public Result<LayerXztbGddbAreaRes> layerXztbGddbAreaRes(LayerYearTableName vo) {
        try {
            return Result.data(gisPgService.layerXztbGddbAreaRes(vo));
        }catch (CustomException e){
            throw e;
        }catch (Exception e){
            log.info("耕地等别统计失败",e);
        }
        throw new CustomException("耕地等别统计失败");
    }

    /**
     * 年份变化
     */
    @Override
    public Result<List<LayerAnnualChangesRes>> layerAnnualChangesRes(LayerAnnualChangesReq vo) {
        try {
            return Result.data(gisPgService.layerAnnualChangesRes(vo));
        }catch (CustomException e){
            throw e;
        }catch (Exception e){
            log.info("查询年度变化错误",e);
        }
        throw new CustomException("查询年度变化错误");
    }

    @Override
    public Result<GisSlopeAspectRes> GisSlopeAspectRes(String lon, String lat) {
        try {
            return Result.data(gisPgService.GisSlopeAspectRes(lon,lat));
        }catch (CustomException e){
            throw e;
        }catch (Exception e){
            log.info("查询坡度坡向错误",e);
        }
        throw new CustomException("查询坡度坡向错误");
    }

    @Override
    public Result<GisFireCombustibleRes> gisFireCombustibleRes(String lon, String lat) {
        try {
            return Result.data(gisPgService.gisFireCombustibleRes(lon,lat));
        }catch (CustomException e){
            throw e;
        }catch (Exception e){
            log.info("查询火势变化错误",e);
        }
        throw new CustomException("查询火势变化错误");
    }

    @Override
    public Result<GisFireGradeRes> gisFireGradeRes(String lon, String lat) {
        try {
            return Result.data(gisPgService.gisFireGradeRes(lon,lat));
        }catch (CustomException e){
            throw e;
        }catch (Exception e){
            log.info("查询火灾等级错误",e);
        }
        throw new CustomException("查询火灾等级错误");
    }

    @Override
    public Result<List<GisVegetationDataRes>> gisVegetationDataRes(String lon, String lat,Integer distance) {
        try {
            return Result.data(gisPgService.gisVegetationDataRes(lon,lat,distance));
        }catch (CustomException e){
            throw e;
        }catch (Exception e){
            log.error("查询植被覆盖错误",e);
        }
        throw new CustomException("查询植被覆盖错误");
    }
}
