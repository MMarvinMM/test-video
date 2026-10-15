package com.chinatower.bdm.modules.inspectionRoute.controller;


import com.alibaba.fastjson.JSON;
import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.modules.inspectionRoute.entity.*;
import com.chinatower.bdm.modules.inspectionRoute.service.IVideoInspectionRouteService;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * video-bdm
 */
@RestController
@Slf4j
@RequestMapping("/video/inspection")
public class VideoInspectionRouteController {

    private static final String VIDEO_INSPECTION_CODE_NOT_NULL_MSG = "巡检路线编码不能为空";

    @Autowired
    private IVideoInspectionRouteService videoInspectionRouteService;

    /**
     * 查询巡检路线列表
     * @param param
     * @return
     */
    @PostMapping("/list")
    public Result<PageInfo<VideoInspectionVO>> list(@RequestBody VideoInspectionDTO param) {
        return videoInspectionRouteService.list(param);
    }

    /**
     * 巡检线路配置分页查询
     * @param inspectionDTO 入参
     * @return Result<PageInfo<InspectionVO>>
     */
    @PostMapping("/queryList")
    public Result<PageInfo<VideoInspectionVO>> queryInspectionRouteList(@RequestBody VideoInspectionDTO inspectionDTO){
        return videoInspectionRouteService.queryListPage(inspectionDTO);
    }

    /**
     * 查询巡检点下图层信息
     * @param param
     * @return
     */
    @PostMapping("/queryRelList")
    public Result<PageInfo<VideoInspectionRelVO>> queryRelList(@RequestBody VideoInspectionRelDTO param) {
        if (Objects.isNull(param.getInspectionRouteId())) {
            return Result.fail(VIDEO_INSPECTION_CODE_NOT_NULL_MSG);
        }
        return videoInspectionRouteService.queryRelList(param);
    }

    /**
     * 查询巡检线路详情信息
     * @param inspectionRouteId
     * @return
     */
    @GetMapping("/detail")
    public Result<VideoInspectionDetailVO> detail(Long inspectionRouteId) {
        if (inspectionRouteId == null) {
            return Result.fail(VIDEO_INSPECTION_CODE_NOT_NULL_MSG);
        }
        return videoInspectionRouteService.detail(inspectionRouteId);
    }

    /**
     * 新增巡检线路信息
     * @param param
     * @return
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody VideoInspectionRouteSaveVO param){
        return videoInspectionRouteService.add(param);
    }

    /**
     * 修改巡检线路信息
     * @param param
     * @return
     */
    @PostMapping("/update")
    public Result<String> update(@RequestBody VideoInspectionRouteSaveVO param){
        return videoInspectionRouteService.update(param);
    }

    /**
     * 删除巡检线路信息
     * @param param
     * @return
     */
    @PostMapping("/delete")
    public Result<String> delete(@RequestBody VideoInspectionDTO param){
        return videoInspectionRouteService.delete(param);
    }

    /**
     * 获取视频巡检路线名称
     * @return
     */
    @RequestMapping("/generateInspectionName")
    public Result<String> generateInspectionName(){
        return videoInspectionRouteService.createInspectionName();
    }

    /**
     * 开启指定巡检路线下具体某一点位编码的流程
     * @param param
     * @return
     */
    @PostMapping("/doProcess")
    public Result<VideoInspectionRouteProcessVO> doProcess(@RequestBody VideoInspectionRouteProcessParam param) {
        try {
            Result<VideoInspectionRouteProcessVO> result = videoInspectionRouteService.doProcess(param);
            log.info("VideoInspectionRouteController doProcess param:{}, result:{}",
                    JSON.toJSONString(param), JSON.toJSONString(result));
            return result;
        } catch (Exception e) {
            log.error("VideoInspectionRouteController doProcess error:", e);
            return Result.success(ResultCode.FAILURE + ":" + e.getMessage());
        }
    }

    /**
     * 自动开启指定巡检路线流程
     * @param param
     * @return
     */
    @PostMapping("/doInspectionProgress")
    public Result<VideoInspectionRouteProcessVO> doInspectionProgress(@RequestBody VideoInspectionRouteProcessParam param) {
        if (Objects.isNull(param.getInspectionRouteId())) {
            return Result.fail(VIDEO_INSPECTION_CODE_NOT_NULL_MSG);
        }
        try {
            Result<VideoInspectionRouteProcessVO> result = videoInspectionRouteService.doInspectionProgress(param);
            log.info("VideoInspectionRouteController doInspectionProgress param:{}, result:{}",
                    JSON.toJSONString(param), JSON.toJSONString(result));
            return result;
        } catch (Exception e) {
            log.error("VideoInspectionRouteController doInspectionProgress error:", e);
            return Result.success(ResultCode.FAILURE + ":" + e.getMessage());
        }
    }

    @PostMapping("/queryRouteRecordInfo")
    public Result<VideoInspectionRouteRecordVO> queryRouteRecordInfo(@RequestBody VideoInspectionRouteRecordParam param) {
        return videoInspectionRouteService.queryRouteRecordInfo(param);
    }
}
