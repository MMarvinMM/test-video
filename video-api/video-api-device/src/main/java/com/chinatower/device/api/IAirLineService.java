package com.chinatower.device.api;

import com.alibaba.fastjson.JSONArray;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.device.api.factory.AirLineFallbackFactory;
import com.chinatower.device.entity.dto.*;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.vo.AirLineVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(contextId = "airLineService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = AirLineFallbackFactory.class)
public interface IAirLineService {
    /**
     * 查询历史轨迹
     *
     */
    @PostMapping("/airResult/queryHisRecord")
    JSONArray queryHisRecord(@RequestBody AirHistoryReq airHistoryReq);

    /**
     * 无人机实时流算法日志补偿重试
     *
     */
    @PostMapping("/air/algorithm/retryAirAlSync")
    Result retryAirAlSync(@RequestBody List<AirAlTaskLogReq> airAlTaskLogReqs);

    /**
     * 查询遥测数据
     */
    @PostMapping("/airResult/queryTelemetryData")
    Result queryTelemetryData(@RequestBody TelemetryReq telemetryReq);

    /**
     * 查询飞行任务的所属节点
     */
    @PostMapping("/airResult/queryTaskNode")
    Result queryTaskNode(@RequestBody AirAlTaskLogReq airAlTaskLogReq);

    /**
     * 退订设备后 关闭设备下的航线任务和算法任务
     * @param airPointToFlyReq
     * @return
     */
    @PostMapping("/air/closeAirTaskAndAlgorithms")
    Result closeAirTaskAndAlgorithms(@RequestBody AirTaskOperateReq airPointToFlyReq);

    /**
     * 续订设备后 重新开启设备下的航线任务和算法任务
     * @param airPointToFlyReq
     * @return
     */
    @PostMapping("/air/openAirTaskAndAlgorithms")
    Result openAirTaskAndAlgorithms(@RequestBody AirTaskOperateReq airPointToFlyReq);

    /**
     * 指点飞行(从机场起飞)
     *
     * @param airPointToFlyReq
     * @return
     */
    @PostMapping("/air/pointToFly")
    Result pointToFly(@RequestBody AirPointToFlyReq airPointToFlyReq);

    /**
     * 批量点飞
     * @param airPointToFlyReq
     * @return
     */
    @PostMapping("/air/pointToFlyBatch")
    Result pointToFlyBatch(@RequestBody List<AirPointToFlyReq> airPointToFlyReq);

    /**
     * 单个返航
     * @param airPointToFlyReq
     * @return
     */
    @PostMapping("/air/flyReturn")
    Result flyReturn(@RequestBody List<AirPointToFlyReq> airPointToFlyReq);

    /**
     *  一键返航批量
     *
     * @param airPointToFlyReq
     * @return
     */
    @PostMapping("/air/flyReturnBatch")
    Result flyReturnBatch(@RequestBody List<AirPointToFlyReq> airPointToFlyReq);

    /**
     * 获取航线详细信息
     * @param airEditDto
     * @return
     */
    @PostMapping("/air/getAirLineInfoByFlightRouteId")
    Result getAirLineInfoByFlightRouteId(@RequestBody AirEditDto airEditDto);

    /**
     * 获取无人机算法配置
     *
     * @param alarmTypeDTO
     * @return
     */
    @PostMapping("/air/getAirAlarmType")
    Result getAirAlarmType(@RequestBody AirAlarmTypeReq alarmTypeDTO);

    /**
     * 新增/编辑航线（包含上传文件）
     *
     */
    @PostMapping(path = "/air/editAirLine", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result editAirLine(@Validated AirEditDto airEditDto, @RequestPart("kmzFile") MultipartFile kmzFile);

    /**
     * 复制航线
     *
     */
    @PostMapping(path = "/air/copyAirLine")
    Result copyAirLine(@RequestBody AirEditDto airEditDto);

    /**
     * 上传航线文件
     * @param kmzFile
     * @return
     */
    @PostMapping(path = "/air/uploadKmzFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result uploadKmzFile(@RequestPart("kmzFile") MultipartFile kmzFile);

    /**
     * 校验航线名称唯一性
     *
     * @param airEditDto
     * @return
     */
    @PostMapping("/air/checkAirLineName")
    Result checkAirLineName(@RequestBody AirEditDto airEditDto);

    /**
     * 校验航线是否被关联
     *
     * @param airEditDto
     * @return
     */
    @PostMapping("/air/checkAirLineBind")
    Result checkAirLineBind(@RequestBody AirEditDto airEditDto);

    /**
     * 查询默认航线名称
     *
     */
    @PostMapping("/air/queryDefultAirLineName")
    Result queryDefultAirLineName(@RequestBody AirEditDto airEditDto);

    /**
     * 航线详情接口
     *
     */
    @PostMapping("/air/getAirLineInfo")
    Result getAirLineInfo(@RequestBody AirEditDto airEditDto);

    /**
     * 修改航线名称
     *
     * @param airEditDto
     * @return
     */
    @PostMapping("/air/editAirLineName")
    Result editAirLineName(@RequestBody AirEditDto airEditDto);

    /**
     * 删除航线
     *
     * @param airEditDto
     * @return
     */
    @PostMapping("/air/delAirLine")
    Result delAirLine(@RequestBody AirEditDto airEditDto);

    /**
     * 航线新增临时算法
     *
     * @param airEditDto
     * @return
     */
    @PostMapping("/air/addTempAlgorithm")
    Result addTempAlgorithm(@RequestBody AirEditDto airEditDto);

    /**
     *
     * 查询航线算法
     * @param airEditDto
     * @return
     */
    @PostMapping("/air/queryAirLineAlgorithm")
    Result queryAirLineAlgorithm(@RequestBody AirEditDto airEditDto);

    /**
     * 航线列表
     *
     * @param airEditDto
     * @return
     */
    @PostMapping("/air/getAirLineList")
    TableDataInfo getAirLineList(@RequestBody AirEditDto airEditDto);


    /**
     * 航线列表
     *
     * @param airEditDto
     * @return
     */
    @PostMapping("/air/getAirLinesList")
    Result getAirLinesList(@RequestBody AirEditDto airEditDto);

    /**
     * 航线列表
     *
     * @param airEditDto
     * @return
     */
    @PostMapping("/air/getAirLinesListSdk")
    Result getAirLinesListSdk(@RequestBody AirEditDto airEditDto);

    /**
     * 添加航线收藏
     *
     * @return
     */
    @PostMapping("/air/addAirLineCollect")
    Result addAirLineCollect(@RequestBody AirEditDto airEditDto);

    /**
     * 查询航线收藏列表
     *
     * @return
     */
    @PostMapping("/air/queryAirLineCollectList")
    TableDataInfo queryAirLineCollectList(@RequestBody AirEditDto airEditDto);

    /**
     * 取消航线收藏
     *
     * @return
     */
    @PostMapping("/air/deleteAirLineCollect")
    Result deleteAirLineCollect(@RequestBody AirEditDto airEditDto);

    /**
     * 获取设备详细信息
     *
     * @param airDeviceInfoDto
     * @return
     */
    @PostMapping("/air/getAirDeviceInfo")
    Result getAirDeviceInfo(@RequestBody AirDeviceInfoReq airDeviceInfoDto);

    /**
     * 获取航点航线详细信息(航线规划使用)
     *
     * @param airEditDto
     * @return
     */
    @PostMapping("/air/getPointInfo")
    Result getPointInfo(@RequestBody AirEditDto airEditDto);

    /**
     * 一键起飞
     *
     * @param airPointToFlyReq
     * @return
     */
    @PostMapping("/air/immediatelyFly")
    Result immediatelyFly(@RequestBody AirPointToFlyReq airPointToFlyReq);

    /**
     * 一键 复飞
     * @param airEditDto
     * @return
     */
    @PostMapping("/air/reFly")
    Result reFly(@Validated AirEditDto airEditDto, @RequestPart("kmzFile") MultipartFile kmzFile);

    /**
     * 一键 复飞(APP)
     * @param airEditDto
     * @return
     */
    @PostMapping("/air/reFlyApp")
    Result reFlyApp(@RequestBody AirEditDto airEditDto);

    /**
     * 查询设备下的航线
     */
    @PostMapping("/air/getAirLineByDeviceCodes")
    Result getAirLineByDevics(@RequestBody AirEditDto airEditDto);

    /**
     * 获取无人机视频实时流地址
     */
    @PostMapping("/airMedium/getVideoRealtimeUrl")
    Result getVideoRealtimeUrl(@RequestBody AirVideoReq airVideoReq);

    /**
     * 切换直播流清晰度
     */
    @PostMapping("/airMedium/switchLiveStream")
    Result switchLiveStream(@RequestBody AirVideoReq airVideoReq);

    /**
     * 无人机查询历史轨迹(分页)
     *
     */
    @PostMapping("/airResult/queryHisRecordLimit")
    Result queryHisRecordLimit(@RequestBody AirHistoryReq airHistoryReq);

    /**
     * 查询任务列表(分页)
     */
    @PostMapping("/airTask/queryTaskListLimit")
    TableDataInfo queryTaskListLimit(@RequestBody AirTaskListReq airTaskReq);

    /**
     * 根据任务获取图片集合
     *
     * @param airResultTaskReq
     * @return
     */
    @PostMapping("/airResult/queryTaskPhotos")
    TableDataInfo queryTaskPhotos(@RequestBody AirResultTaskReq airResultTaskReq);

    /**
     * 根据任务获取图片集合(行业sdk，需要传租户行业编码)
     *
     * @param airResultTaskReq
     * @return
     */
    @PostMapping("/airResult/queryTaskPhotosSdk")
    TableDataInfo queryTaskPhotosSdk(@RequestBody AirResultTaskReq airResultTaskReq);

    /**
     * 根据任务获取视频集合
     *
     * @param airResultTaskReq
     * @return
     */
    @PostMapping("/airResult/queryTaskVideos")
    TableDataInfo queryTaskVideos(@RequestBody AirResultTaskReq airResultTaskReq);

    /**
     * 批量删除图片/视频成果物
     *
     * @param airResultReq
     * @return
     */
    @PostMapping("/airResult/taskRecords/delete")
    Result photoVideoDelete(@RequestBody AirResultReq airResultReq);

    /**
     * 重命名图片/视频成果物
     *
     * @param airResultRenameReq
     * @return
     */
    @PostMapping("/airResult/taskRecords/rename")
    Result photoVideoRename(@RequestBody AirResultRenameReq airResultRenameReq);

    /**
     * 获取飞行结果任务列表
     *
     * @param airResultTaskReq
     * @return
     */
    @PostMapping("/airResult/queryTaskList")
    TableDataInfo queryTaskList(@RequestBody AirResultTaskReq airResultTaskReq);

    /**
     * 获取飞行结果任务列表(行业sdk,入参需要传租户和行业编码)
     *
     * @param airResultTaskReq
     * @return
     */
    @PostMapping("/airResult/queryTaskListSdk")
    TableDataInfo queryTaskListSdk(@RequestBody AirResultTaskReq airResultTaskReq);

    /**
     * 通过视频时间查询轨迹
     *
     * @param airResultTaskReq
     * @return
     */
    @PostMapping("/airResult/queryHisRecordByVideoTime")
    Result queryHisRecordByVideoTime(@RequestBody AirResultTaskReq airResultTaskReq);

    /**
     * 查询历史轨迹 前端地图展示用
     *
     * @param airHistoryReq
     * @return
     */
    @PostMapping("/airResult/queryHisRecords")
    Result queryHisRecords(@RequestBody AirHistoryReq airHistoryReq);

    /**
     * 查询历史轨迹 视频融合
     *
     * @param airHistoryReq
     * @return
     */
    @PostMapping("/airResult/queryHisRecordsBySelf")
    Result queryHisRecordsBySelf(@RequestBody AirHistoryReq airHistoryReq);

    /**
     * 同步数据源
     */
    @PostMapping("/airResult/mediaSync")
    Result mediaSync(@RequestBody AirResultTaskReq airResultTaskReq);

    /**
     * 无人机树默认选中有最新执行任务的设备
     */
    @PostMapping("/airResult/queryDefultUav")
    Result queryDefultUav();

    /**
     * 视频展示-视频抽帧
     * @param videoExtractReq
     * @return
     */
    @PostMapping("/airResult/doVideoExtraction")
    Result doVideoExtraction(@RequestBody AirVideoExtractReq videoExtractReq);

    /**
     * 视频展示-查询抽帧列表
     * @param videoExtractReq
     * @return
     */
    @PostMapping("/airResult/queryVideoExtractionList")
    Result queryVideoExtractionList(@RequestBody AirVideoExtractReq videoExtractReq);

    /**
     * 视频展示-抽帧进度
     * @param extractReq
     * @return
     */
    @PostMapping("/airResult/queryExtractionProgress")
    Result queryExtractionProgress(@RequestBody AirVideoExtractReq extractReq);

    /**
     * 视频展示-删除抽帧任务图片
     * @param extractTaskPhotoReq
     * @return
     */
    @PostMapping("/airResult/delExtractTaskPhotos")
    Result delExtractTaskPhotos(@RequestBody AirExtractTaskPhotoReq extractTaskPhotoReq);

    /**
     * 视频抽帧-取消抽帧
     * @param extractTaskReq
     * @return
     */
    @PostMapping("/airResult/cancelExtractTask")
    Result cancelExtractTask(@RequestBody AirVideoExtractTaskReq extractTaskReq);

    /**
     * 收藏飞行结果
     * @param airResultReq
     * @return
     */
    @PostMapping("/airResult/addCollectResult")
    Result addCollectResult(@RequestBody AirResultReq airResultReq);

    /**
     * 取消收藏飞行结果
     * @param airResultReq
     * @return
     */
    @PostMapping("/airResult/delCollectResult")
    Result delCollectResult(@RequestBody AirResultReq airResultReq);

    /**
     * 查询收藏结果列表
     */
    @PostMapping("/airResult/queryCollectResultList")
    TableDataInfo queryCollectResultList(@RequestBody AirResultReq airResultReq);

    /**
     * 删除收藏成果
     */
    @PostMapping("/airResult/delCollectResultByIds")
    Result delCollectResultById(@RequestBody AirResultReq airResultReq);

    /**
     * 收藏链接 分享加解密
     */
    @PostMapping("/airResult/secretJson")
    Result secretJson(@RequestBody AirResultReq airResultReq);

    /**
     * 查询照片/视频的详情
     */
    @PostMapping("/airResult/queryPhotoVideoAttr")
    Result queryPhotoVideoAttr(@RequestBody AirResultReq airResultReq);

    /**
     * 查询复飞任务结果集
     */
    @PostMapping("/airResult/queryReFlightTask")
    Result queryReFlightTask(@RequestBody AirResultReq airResultReq);

    /**
     * 获取任务4.3.18.获取任务里图片/视频基础信息(APP日视图)
     */
    @PostMapping("/airResult/queryTaskPhotoVideoInfo")
    TableDataInfo queryTaskPhotoVideoInfo(@RequestBody QueryTaskPhotoVideoReq airResultReq);

    /**
     * 查询图片列表 查询参数 对照点id和任务id和经纬度
     */
    @PostMapping("/airResult/queryCorrespondPhotoList")
    Result queryCorrespondPhotoList(@RequestBody CorrespondPointReq correspondPointReq);

    /**
     * 查询飞行任务下拉列表
     */
    @PostMapping("/airResult/queryFlightTaskList")
    Result queryFlightTaskList(@RequestBody AirResultReq airResultReq);

    /**
     * 查询用户订购无人机信息
     */
    @PostMapping("/air/queryUserDeviceOrder")
    Result queryUserDeviceOrder(@RequestBody TelemetryReq telemetryReq);

    /**
     * 根据任务id查询航线
     * @param airTaskReq
     * @return
     */
    @PostMapping("/air/queryAirLineByTaskId")
    Result<AirLineVo> queryAirLineByTaskId(@RequestBody AirTaskReq airTaskReq);
}
