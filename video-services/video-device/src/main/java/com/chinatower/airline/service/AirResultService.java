package com.chinatower.airline.service;

import com.alibaba.fastjson.JSONArray;
import com.chinatower.airline.dto.request.*;
import com.chinatower.airline.dto.request.AirResultTaskReq;
import com.chinatower.airline.dto.response.AirTaskPhotoRep;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.*;

import java.text.ParseException;
import java.util.List;


/**
 * @Author: csj
 * @Date: 2024/11/4 10:26
 */
public interface AirResultService {
    /**
     * 批量删除图片/视频成果物
     *
     * @param airResultDto
     * @return
     */
    Result photoVideoDelete(AirResultReq airResultDto);

    /**
     * 重命名图片/视频成果物
     *
     * @param airResultRenameReq
     * @return
     */
    Result photoVideoRename(AirResultRenameReq airResultRenameReq);

    /**
     * 获取飞行结果任务列表
     *
     * @param airResultTaskReq
     * @param flag
     * @return
     */
    TableDataInfo queryTaskList(AirResultTaskReq airResultTaskReq, String flag);

    /**
     * 根据任务获取图片集合
     *
     * @param airResultTaskReq
     * @return
     */
    TableDataInfo queryTaskPhotos(AirResultTaskReq airResultTaskReq);

    /**
     * 根据任务获取视频集合
     *
     * @param airResultTaskReq
     * @return
     */
    TableDataInfo queryTaskVideos(AirResultTaskReq airResultTaskReq);

    /**
     * 同步数据源
     *
     * @param airResultTaskReq
     * @return
     */
    Result mediaSync(AirResultTaskReq airResultTaskReq);

    /**
     * 通过视频时间查询轨迹
     *
     * @param airResultTaskReq
     * @return
     */
    Result queryHisRecordByVideoTime(AirResultTaskReq airResultTaskReq);

    /**
     * 通过视频时间查询轨迹
     *
     * @param airResultTaskReq
     * @return
     */
    JSONArray queryHisRecord(AirResultTaskReq airResultTaskReq);

    /**
     *
     * @return
     */
    Result queryDefultUav(AirUavDeviceReq airTaskReq);

    /**
     * 视频抽帧
     * @param videoExtractReq
     * @return
     */
    Result doVideoExtraction(AirVideoExtractReq videoExtractReq);

    /**
     * 查询抽帧列表
     * @param videoExtractReq
     * @return
     */
    Result queryVideoExtractionList(AirVideoExtractReq videoExtractReq);

    /**
     * 抽帧进度
     * @param extractReq
     * @return
     */
    Result queryExtractionProgress(AirVideoExtractReq extractReq);

    /**
     *  删除抽帧任务图片
     * @param extractTaskPhotoReq
     * @return
     */
    Result delExtractTaskPhotos(AirExtractTaskPhotoReq extractTaskPhotoReq);

    /**
     * 取消抽帧
     * @param extractTaskReq
     * @return
     */
    Result cancelExtractTask(AirVideoExtractTaskReq extractTaskReq);

    /**
     * 查询任务
     * @param telemetryReq
     * @return
     */
    Result queryTelemetryData(TelemetryReq telemetryReq);

    /**
     * 查询任务节点
     * @param airAlTaskLogReq
     * @return
     */
    Result queryTaskNode(AirAlTaskLogReq airAlTaskLogReq);

    /**
     * 添加收藏结果
     * @param airResultReq
     * @return
     */
    Result addCollectResult(AirResultReq airResultReq);

    /**
     * 删除收藏结果
     * @param airResultReq
     * @return
     */
    Result delCollectResult(AirResultReq airResultReq);

    /**
     * 查询收藏结果
     * @param airResultReq
     * @return
     */
    List<AirTaskPhotoRep> queryCollectResultList(AirResultReq airResultReq);

    /**
     *
     * @param airResultReq
     * @return
     */
    Result delCollectResultById(AirResultReq airResultReq);

    /**
     *
     * @param airResultReq
     * @return
     */
    Result secretJson(AirResultReq airResultReq);

    /**
     * 查询图片/视频属性
     * @param airResultReq
     * @return
     */
    Result queryPhotoVideoAttr(AirResultReq airResultReq);

    /**
     * 查询重放任务
     * @param airResultReq
     * @return
     */
    Result queryReFlightTask(AirResultReq airResultReq);

    /**
     * 查询任务图片/视频信息
     * @param airResultReq
     * @return
     */
    TableDataInfo queryTaskPhotoVideoInfo(QueryTaskPhotoVideoReq airResultReq);

    /**
     * 查询对应图片列表
     * @param correspondPointReq
     * @return
     */
    Result queryCorrespondPhotoList(CorrespondPointReq correspondPointReq);

    /**
     * 查询飞行任务下拉列表 租户全量权限下
     * @param airResultReq
     * @return
     */
    Result queryFlightTaskList(AirResultReq airResultReq);

    /**
     * 查询历史数据分页
     * @param airHistoryReq
     * @return
     */
    List<UavFlyReq> queryHisRecordLimit(AirHistoryReq airHistoryReq);

    /**
     * 查询历史数据
     * @param airHistoryReq
     * @return
     */
    List<UavFlyReq> queryHisRecordsBySelf(AirHistoryReq airHistoryReq);

    /**
     * 根据设备类型查询照片(摄像机/无人机)
     * @param queryPhotosTypesReq
     * @return
     */
    TableDataInfo queryPhotosByType(QueryPhotosTypesReq queryPhotosTypesReq);

    /**
     * 根据登陆用户信息查询收藏的成果物的任务id集合
     * @param queryClollectReq
     * @return
     */
    Result<List<String>> queryTaskRecordIds(QueryCollectReq queryClollectReq);
}
