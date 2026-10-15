package com.chinatower.airline.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.airline.dto.request.AlgorithmReq;
import com.chinatower.airline.dto.request.CompareImageReq;
import com.chinatower.device.entity.dto.AnnotationMapReq;
import com.chinatower.device.entity.dto.CorrespondPointReq;
import com.chinatower.device.entity.dto.FlyAreaReq;
import com.chinatower.airline.service.AirMapService;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static com.chinatower.common.core.utils.StringUtils.getDataTable;

/**
 * @author WLF
 * @description
 * @date 2025/1/16 13:12
 */
@RestController
@Api(value = "无人机地图", tags = "无人机地图")
@Slf4j
@RequestMapping("/airMap")
public class AirMapController {
    @Resource
    private AirMapService airMapService;

    /**
     *  无人机地图标注(客户级别)
     *
     * @param annotationMapReq
     * @return
     */
    @PostMapping("/addAnnotationMap")
    public Result addAnnotationMap(@RequestBody AnnotationMapReq annotationMapReq) {
        log.info("AirMapController-addAnnotationMap-req:{}", JSON.toJSONString(annotationMapReq));
        return airMapService.addAnnotationMap(annotationMapReq);
    }

    /**
     * 无人机地图标注查询(客户级别)
     * @param annotationMapReq
     * @return
     */
    @PostMapping("/queryAnnotationMapList")
    public TableDataInfo queryAnnotationMapList(@RequestBody AnnotationMapReq annotationMapReq) {
        log.info("AirMapController-queryAnnotationMap-req:{}", JSON.toJSONString(annotationMapReq));
        return getDataTable(airMapService.queryAnnotationMap(annotationMapReq));
    }

    /**
     * 无人机地图标注删除(客户级别)
     * @param annotationMapReq
     * @return
     */
    @PostMapping("/delAnnotationMap")
    public Result delAnnotationMap(@RequestBody AnnotationMapReq annotationMapReq) {
        log.info("AirMapController-delAnnotationMap-req:{}", JSON.toJSONString(annotationMapReq));
        return airMapService.delAnnotationMap(annotationMapReq);
    }

    /**
     * 无人机地图标注默认名称(客户级别)
     * @param annotationMapReq
     * @return
     */
    @PostMapping("/defaultAnnotationMapName")
    public Result defaultAnnotationMapName(@RequestBody AnnotationMapReq annotationMapReq) {
        log.info("AirMapController-defaultAnnotationMapName-req:{}", JSON.toJSONString(annotationMapReq));
        return airMapService.defaultAnnotationMapName(annotationMapReq);
    }

    /**
     * 无人机地图标注默认名称(客户级别)
     * @param annotationMapReq
     * @return
     */
    @PostMapping("/checkAnnotationMapName")
    public Result checkAnnotationMapName(@RequestBody AnnotationMapReq annotationMapReq) {
        log.info("AirMapController-checkAnnotationMapName-req:{}", JSON.toJSONString(annotationMapReq));
        return airMapService.checkAnnotationMapName(annotationMapReq);
    }

    /**
     * 添加飞行区(客户级别)
     * @param flyAreaReq
     * @return
     */
    @PostMapping("/addFlyArea")
    public Result addFlyArea(@RequestBody FlyAreaReq flyAreaReq) {
        log.info("AirMapController-addFlyArea-req:{}", JSON.toJSONString(flyAreaReq));
        return airMapService.addFlyArea(flyAreaReq);
    }

    /**
     * 删除飞行区(客户级别)
     * @param flyAreaReq
     * @return
     */
    @PostMapping("/delFlyArea")
    public Result delFlyArea(@RequestBody FlyAreaReq flyAreaReq) {
        log.info("AirMapController-delFlyArea-req:{}", JSON.toJSONString(flyAreaReq));
        return airMapService.delFlyArea(flyAreaReq);
    }

    /**
     * 飞行区列表(客户级别)
     * @param flyAreaReq
     * @return
     */
    @PostMapping("/queryFlyAreaList")
    public TableDataInfo queryFlyAreaList(@RequestBody FlyAreaReq flyAreaReq) {
        log.info("AirMapController-queryFlyAreaList-req:{}", JSON.toJSONString(flyAreaReq));
        return getDataTable(airMapService.queryFlyAreaList(flyAreaReq));
    }

    /**
     * 飞行区列表名称校验(客户级别)
     * @param flyAreaReq
     * @return
     */
    @PostMapping("/checkFlyAreaName")
    public Result checkFlyAreaName(@RequestBody FlyAreaReq flyAreaReq) {
        log.info("AirMapController-checkAnnotationMapName-req:{}", JSON.toJSONString(flyAreaReq));
        return airMapService.checkFlyAreaName(flyAreaReq);
    }

    /**
     * 飞行区编辑(客户级别)
     * @param flyAreaReq
     * @return
     */
    @PostMapping("/editFlyArea")
    public Result editFlyArea(@RequestBody FlyAreaReq flyAreaReq) {
        log.info("AirMapController-editFlyArea-req:{}", JSON.toJSONString(flyAreaReq));
        return airMapService.editFlyArea(flyAreaReq);
    }

    /**
     * 对照点 新增/编辑
     */
    @PostMapping("/editCorrespondPoint")
    public Result editCorrespondPoint(@RequestBody CorrespondPointReq correspondPointReq) {
        log.info("AirMapController-editCorrespondPoint-req:{}", JSON.toJSONString(correspondPointReq));
        return airMapService.editCorrespondPoint(correspondPointReq);
    }

    /**
     * 对照点 删除
     */
    @PostMapping("/delCorrespondPoint")
    public Result delCorrespondPoint(@RequestBody CorrespondPointReq correspondPointReq) {
        log.info("AirMapController-delCorrespondPoint-req:{}", JSON.toJSONString(correspondPointReq));
        return airMapService.delCorrespondPoint(correspondPointReq);
    }

    /**
     * 对照点 列表
     */
    @PostMapping("/queryCorrespondPointList")
    public TableDataInfo queryCorrespondPointList(@RequestBody CorrespondPointReq correspondPointReq) {
        log.info("AirMapController-queryCorrespondPointList-req:{}", JSON.toJSONString(correspondPointReq));
        return getDataTable(airMapService.queryCorrespondPointList(correspondPointReq));
    }

    /**
     * 对照点唯一名称校验
     */
    @PostMapping("/checkCorrespondPointName")
    public Result checkCorrespondPointName(@RequestBody CorrespondPointReq correspondPointReq) {
        log.info("AirMapController-checkCorrespondPointName-req:{}", JSON.toJSONString(correspondPointReq));
        return airMapService.checkCorrespondPointName(correspondPointReq);
    }

    /**
     * 查询禁飞区下的航线
     */
    @PostMapping("/queryAirLineByFlyArea")
    public TableDataInfo queryAirLineByFlyArea(@RequestBody FlyAreaReq flyAreaReq) {
        log.info("AirMapController-queryAirLineByFlyArea-req:{}", JSON.toJSONString(flyAreaReq));
        return getDataTable(airMapService.queryAirLineByFlyArea(flyAreaReq));
    }

    /**
     * 收藏/取消收藏 图片对比图片信息
     */
    @PostMapping("/compareImageCollectEdit")
    public Result compareImageCollectEdit(@RequestBody CompareImageReq compareImageReq) {
        log.info("AirMapController-compareImageCollectEdit-req:{}", JSON.toJSONString(compareImageReq));
        return airMapService.compareImageCollectEdit(compareImageReq);
    }

    /**
     * 查询算法识别结果
     */
    @PostMapping("/queryAlgorithmsResult")
    public Result queryAlgorithmsResult(@RequestBody AlgorithmReq algorithmReq) {
        log.info("AirMapController-queryAlgorithmsResult-req:{}", JSON.toJSONString(algorithmReq));
        return Result.data(airMapService.queryAlgorithmsResult(algorithmReq));
    }
}
