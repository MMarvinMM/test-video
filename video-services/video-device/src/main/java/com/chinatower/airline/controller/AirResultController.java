package com.chinatower.airline.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.chinatower.airline.dto.request.AirResultTaskReq;
import com.chinatower.airline.dto.request.AirUavDeviceReq;
import com.chinatower.airline.dto.request.QueryCollectReq;
import com.chinatower.airline.dto.request.QueryPhotosTypesReq;
import com.chinatower.airline.service.AirResultService;
import com.chinatower.airline.util.UavOkhttpUtil;
import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.device.constants.Constants;
import com.chinatower.device.entity.dto.*;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author csj
 * @description
 * @date 2024/11/3 21:13
 */

@RestController
@Api(value = "飞行结果模块", tags = "飞行结果模块")
@Slf4j
@RequestMapping("/airResult")
public class AirResultController extends BaseController {

    @Resource
    private AirResultService airResultService;
    @Resource
    private UavOkhttpUtil uavOkhttpUtil;

    /**
     * 批量删除图片/视频成果物
     *
     * @param airResultReq
     * @return
     */
    @PostMapping("/taskRecords/delete")
    public Result photoVideoDelete(@RequestBody AirResultReq airResultReq) {
        log.info("AirResultController-photoVideoDelete-req:{}", JSON.toJSONString(airResultReq));
        return airResultService.photoVideoDelete(airResultReq);
    }

    /**
     * 重命名图片/视频成果物
     *
     * @param airResultRenameReq
     * @return
     */
    @PostMapping("/taskRecords/rename")
    public Result photoVideoRename(@RequestBody AirResultRenameReq airResultRenameReq) {
        log.info("AirResultController-photoVideoRename-req:{}", JSON.toJSONString(airResultRenameReq));
        return airResultService.photoVideoRename(airResultRenameReq);
    }

    /**
     * 获取飞行结果任务列表
     *
     * @param airResultTaskReq
     * @return
     */
    @PostMapping("/queryTaskList")
    public TableDataInfo queryTaskList(@RequestBody AirResultTaskReq airResultTaskReq, HttpServletRequest request) {
        log.info("AirResultController-queryTaskList-req:{}", JSON.toJSONString(airResultTaskReq));
        this.getPlatFormCpde(airResultTaskReq);
        return airResultService.queryTaskList(airResultTaskReq, "1");
    }

    /**
     * 获取飞行结果任务列表
     *
     * @param airResultTaskReq
     * @return
     */
    @PostMapping("/queryTaskListSdk")
    public TableDataInfo queryTaskListSdk(@RequestBody AirResultTaskReq airResultTaskReq, HttpServletRequest request) {
        log.info("AirResultController-queryTaskListSDK-req:{}", JSON.toJSONString(airResultTaskReq));
        String platformCode = uavOkhttpUtil.getSelfPlatFormCode();
        airResultTaskReq.setAcceptNode(platformCode);
        airResultTaskReq.setIsCollect(Constants.ZERO);
        return airResultService.queryTaskList(airResultTaskReq, "1");
    }

    /**
     * 根据任务获取图片集合
     *
     * @param airResultTaskReq
     * @return
     */
    @PostMapping("/queryTaskPhotos")
    public TableDataInfo queryTaskPhotos(@RequestBody AirResultTaskReq airResultTaskReq, HttpServletRequest request) {
        log.info("AirResultController-queryTaskPhotos-req:{}", JSON.toJSONString(airResultTaskReq));
        this.getPlatFormCpde(airResultTaskReq);
        return airResultService.queryTaskPhotos(airResultTaskReq);
    }

    /**
     * 根据任务获取图片集合
     *
     * @param airResultTaskReq
     * @return
     */
    @PostMapping("/queryTaskPhotosSdk")
    public TableDataInfo queryTaskPhotosSdk(@RequestBody AirResultTaskReq airResultTaskReq, HttpServletRequest request) {
        log.info("AirResultController-queryTaskPhotosSDK-req:{}", JSON.toJSONString(airResultTaskReq));
        String platformCode = uavOkhttpUtil.getSelfPlatFormCode();
        airResultTaskReq.setAcceptNode(platformCode);
        return airResultService.queryTaskPhotos(airResultTaskReq);
    }

    /**
     * 根据任务获取视频集合
     *
     * @param airResultTaskReq
     * @return
     */
    @PostMapping("/queryTaskVideos")
    public TableDataInfo queryTaskVideos(@RequestBody AirResultTaskReq airResultTaskReq, HttpServletRequest request) {
        log.info("AirResultController-queryTaskVideos-req:{}", JSON.toJSONString(airResultTaskReq));
        this.getPlatFormCpde(airResultTaskReq);
        return airResultService.queryTaskVideos(airResultTaskReq);
    }

    /**
     * 从header中获取行业和租户信息
     * @param airResultTaskReq
     * @param
     */
    private void getPlatFormCpde(AirResultTaskReq airResultTaskReq) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        // 获取平台节点
        String platformCode = uavOkhttpUtil.getSelfPlatFormCode();
        airResultTaskReq.setAcceptNode(platformCode);
        String userId = currentUser.getUser().getUserId();
        airResultTaskReq.setUserId(userId);
    }

    /**
     * 通过视频时间查询轨迹
     *
     * @param airResultTaskReq
     * @return
     */
    @PostMapping("/queryHisRecordByVideoTime")
    public Result queryHisRecordByVideoTime(@RequestBody AirResultTaskReq airResultTaskReq) {
        log.info("AirResultController-queryHisRecordByVideoTime-req:{}", JSON.toJSONString(airResultTaskReq));
        return airResultService.queryHisRecordByVideoTime(airResultTaskReq);
    }

    /**
     * 查询历史轨迹
     *
     * @param airHistoryReq
     * @return
     */
    @PostMapping("/queryHisRecord")
    public JSONArray queryHisRecord(@RequestBody AirHistoryReq airHistoryReq) {
        log.info("AirResultController-queryHisRecord-req:{}", JSON.toJSONString(airHistoryReq));
        AirResultTaskReq resultTaskReq = new AirResultTaskReq();
        resultTaskReq.setTaskRecordId(airHistoryReq.getTaskRecordId());
        return airResultService.queryHisRecord(resultTaskReq);
    }

    /**
     * 查询历史轨迹 前端地图展示用
     *
     * @param airHistoryReq
     * @return
     */
    @PostMapping("/queryHisRecords")
    public Result queryHisRecords(@RequestBody AirHistoryReq airHistoryReq) {
        log.info("AirResultController-queryHisRecord-req:{}", JSON.toJSONString(airHistoryReq));
        AirResultTaskReq resultTaskReq = new AirResultTaskReq();
        resultTaskReq.setTaskRecordId(airHistoryReq.getTaskRecordId());
        resultTaskReq.setAccessNode(airHistoryReq.getAccessNode());
        JSONArray jsonArray =  airResultService.queryHisRecord(resultTaskReq);
        return Result.data(jsonArray);
    }

    /**
     * 查询历史轨迹 视频融合
     *
     * @param airHistoryReq
     * @return
     */
    @PostMapping("/queryHisRecordsBySelf")
    public Result queryHisRecordsBySelf(@RequestBody AirHistoryReq airHistoryReq) {
        log.info("AirResultController-queryHisRecordsBySelf-req:{}", JSON.toJSONString(airHistoryReq));
        List<UavFlyReq> uavUfcpInfoReq =  airResultService.queryHisRecordsBySelf(airHistoryReq);
        return Result.data(uavUfcpInfoReq);
    }

    /**
     * 查询历史轨迹分页 sdk
     *
     * @param airHistoryReq
     * @return
     */
    @PostMapping("/queryHisRecordLimit")
    public Result queryHisRecordLimit(@RequestBody AirHistoryReq airHistoryReq) {
        log.info("AirResultController-queryHisRecordLimit-req:{}", JSON.toJSONString(airHistoryReq));
        List<UavFlyReq> uavUfcpInfoReq =  airResultService.queryHisRecordLimit(airHistoryReq);
        return Result.data(uavUfcpInfoReq);
    }

    /**
     * 同步数据源
     */
    @PostMapping("/mediaSync")
    public Result mediaSync(@RequestBody AirResultTaskReq airResultTaskReq) {
        log.info("AirResultController-mediaSync-req:{}", JSON.toJSONString(airResultTaskReq));
        return airResultService.mediaSync(airResultTaskReq);
    }

    /**
     * 无人机树默认选中有最新执行任务的设备
     */
    @PostMapping("/queryDefultUav")
    public Result queryDefultUav() {
        AirUavDeviceReq airUavDevice = new AirUavDeviceReq();
        return airResultService.queryDefultUav(airUavDevice);
    }

    /**
     * 更新视频提取状态
     * @return
     */
    @PostMapping("/updateVideoExtractStatus")
    public Result updateVideoExtractStatus() {
        return Result.success("");
    }

    /**
     * 视频展示-视频抽帧
     * @param videoExtractReq
     * @return
     */
    @PostMapping("/doVideoExtraction")
    public Result doVideoExtraction(@RequestBody AirVideoExtractReq videoExtractReq) {
        log.info("AirResultController-doVideoExtraction-req:{}", JSON.toJSONString(videoExtractReq));
        return airResultService.doVideoExtraction(videoExtractReq);
    }

    /**
     * 视频展示-查询抽帧列表
     * @param videoExtractReq
     * @return
     */
    @PostMapping("/queryVideoExtractionList")
    public Result queryVideoExtractionList(@RequestBody AirVideoExtractReq videoExtractReq) {
        log.info("AirResultController-queryVideoExtractionList-req:{}", JSON.toJSONString(videoExtractReq));
        return airResultService.queryVideoExtractionList(videoExtractReq);
    }

    /**
     * 视频展示-抽帧进度
     * @param extractReq
     * @return
     */
    @PostMapping("/queryExtractionProgress")
    public Result queryExtractionProgress(@RequestBody AirVideoExtractReq extractReq) {
        log.info("AirResultController-queryExtractionProgress-req:{}", JSON.toJSONString(extractReq));
        return airResultService.queryExtractionProgress(extractReq);
    }

    /**
     * 视频展示-删除抽帧任务图片
     * @param extractTaskPhotoReq
     * @return
     */
    @PostMapping("/delExtractTaskPhotos")
    public Result delExtractTaskPhotos(@RequestBody AirExtractTaskPhotoReq extractTaskPhotoReq) {
        log.info("AirResultController-delExtractTaskPhotos-req:{}", JSON.toJSONString(extractTaskPhotoReq));
        return airResultService.delExtractTaskPhotos(extractTaskPhotoReq);
    }

    /**
     * 视频抽帧-取消抽帧
     * @param extractTaskReq
     * @return
     */
    @PostMapping("/cancelExtractTask")
    public Result cancelExtractTask(@RequestBody AirVideoExtractTaskReq extractTaskReq) {
        log.info("AirResultController-cancelExtractTask-req:{}", JSON.toJSONString(extractTaskReq));
        return airResultService.cancelExtractTask(extractTaskReq);
    }

    /**
     * 查询遥测数据
     * @param
     * @return
     */
    @PostMapping("/queryTelemetryData")
    public Result queryTelemetryData(@RequestBody TelemetryReq telemetryReq) {
        log.info("AirResultController-queryTelemetryData-req:{}", JSON.toJSONString(telemetryReq));
        return airResultService.queryTelemetryData(telemetryReq);
    }

    /**
     * 查询飞行任务的所属节点
     */
    @PostMapping("/queryTaskNode")
    public Result queryTaskNode(@RequestBody AirAlTaskLogReq airAlTaskLogReq) {
        log.info("AirResultController-queryTaskNode-req:{}", JSON.toJSONString(airAlTaskLogReq));
        return airResultService.queryTaskNode(airAlTaskLogReq);
    }

    /**
     * 收藏飞行结果
     * @param airResultReq
     * @return
     */
    @PostMapping("/addCollectResult")
    public Result addCollectResult(@RequestBody AirResultReq airResultReq) {
        log.info("AirResultController-addCollectResult-req:{}", JSON.toJSONString(airResultReq));
        return airResultService.addCollectResult(airResultReq);
    }

    /**
     * 取消收藏飞行结果
     * @param airResultReq
     * @return
     */
    @PostMapping("/delCollectResult")
    public Result delCollectResult(@RequestBody AirResultReq airResultReq) {
        log.info("AirResultController-delCollectResult-req:{}", JSON.toJSONString(airResultReq));
        return airResultService.delCollectResult(airResultReq);
    }

    /**
     * 查询收藏结果列表
     */
    @PostMapping("/queryCollectResultList")
    public TableDataInfo queryCollectResultList(@RequestBody AirResultReq airResultReq) {
        log.info("AirResultController-queryCollectResultList-req:{}", JSON.toJSONString(airResultReq));
        return getDataTable(airResultService.queryCollectResultList(airResultReq));
    }

    /**
     * 删除收藏成果
     */
    @PostMapping("/delCollectResultByIds")
    public Result delCollectResultById(@RequestBody AirResultReq airResultReq) {
        log.info("AirResultController-delCollectResultById-req:{}", JSON.toJSONString(airResultReq));
        return airResultService.delCollectResultById(airResultReq);
    }

    /**
     * 收藏链接 分享加解密
     */
    @PostMapping("/secretJson")
    public Result secretJson(@RequestBody AirResultReq airResultReq) {
        log.info("AirResultController-secretJson-req:{}", JSON.toJSONString(airResultReq));
        return airResultService.secretJson(airResultReq);
    }

    /**
     * 查询照片/视频的详情
     */
    @PostMapping("/queryPhotoVideoAttr")
    public Result queryPhotoVideoAttr(@RequestBody AirResultReq airResultReq) {
        log.info("AirResultController-queryPhotoVideoAttr-req:{}", JSON.toJSONString(airResultReq));
        return airResultService.queryPhotoVideoAttr(airResultReq);
    }

    /**
     * 查询复飞任务结果集
     */
    @PostMapping("/queryReFlightTask")
    public Result queryReFlightTask(@RequestBody AirResultReq airResultReq) {
        log.info("AirResultController-queryReFlightTask-req:{}", JSON.toJSONString(airResultReq));
        return airResultService.queryReFlightTask(airResultReq);
    }

    /**
     * 获取任务4.3.18.获取任务里图片/视频基础信息(APP日视图)
     */
    @PostMapping("/queryTaskPhotoVideoInfo")
    public TableDataInfo queryTaskPhotoVideoInfo(@RequestBody QueryTaskPhotoVideoReq airResultReq) {
        log.info("AirResultController-queryTaskPhotoVideoInfo-req:{}", JSON.toJSONString(airResultReq));
        return airResultService.queryTaskPhotoVideoInfo(airResultReq);
    }

    /**
     * 查询图片列表 查询参数 对照点id和任务id和经纬度
     */
    @PostMapping("/queryCorrespondPhotoList")
    public Result queryCorrespondPhotoList(@RequestBody CorrespondPointReq correspondPointReq) {
        log.info("AirResultController-queryCorrespondPhotoList-req:{}", JSON.toJSONString(correspondPointReq));
        return airResultService.queryCorrespondPhotoList(correspondPointReq);
    }

    /**
     * 查询飞行任务下拉列表
     */
    @PostMapping("/queryFlightTaskList")
    public Result queryFlightTaskList(@RequestBody AirResultReq airResultReq) {
        log.info("AirResultController-queryFlightTaskList-req:{}", JSON.toJSONString(airResultReq));
        return airResultService.queryFlightTaskList(airResultReq);
    }

    /**
     * 根据设备类型查询照片(摄像机/无人机)
     * @param queryPhotosTypesReq
     * @return
     */
    @PostMapping("/queryPhotosByType")
    public TableDataInfo queryPhotosByType(@RequestBody QueryPhotosTypesReq queryPhotosTypesReq) {
        log.info("AirResultController-queryPhotosByType-req:{}", JSON.toJSONString(queryPhotosTypesReq));
        return airResultService.queryPhotosByType(queryPhotosTypesReq);
    }

    /**
     * 根据登陆用户信息查询收藏的成果物的任务id集合
     */
    @PostMapping("/queryCollectTaskIds")
    public Result<List<String>> queryCollectTaskIds(@RequestBody QueryCollectReq queryClollectReq) {
        log.info("AirResultController-queryTaskRecordIds-req:{}", JSON.toJSONString(queryClollectReq));
        return airResultService.queryTaskRecordIds(queryClollectReq);
    }
}
