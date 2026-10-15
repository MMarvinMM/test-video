package com.chinatower.device.api.factory;

import com.alibaba.fastjson.JSONArray;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.device.api.IAirLineService;
import com.chinatower.device.entity.dto.*;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.vo.AirLineVo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 降级处理
 */
@Component
public class AirLineFallbackFactory implements FallbackFactory<IAirLineService> {
    @Override
    public IAirLineService create(Throwable throwable) {
        return new IAirLineService() {
            @Override
            public JSONArray queryHisRecord(AirHistoryReq airHistoryReq) {
                return null;
            }

            @Override
            public Result retryAirAlSync(List<AirAlTaskLogReq> airAlTaskLogReqs) {
                return Result.fail("无人机实时流算法日志补偿重试失败:" + throwable.getMessage());
            }

            @Override
            public Result queryTelemetryData(TelemetryReq telemetryReq) {
                return Result.fail("查询遥测数据失败:" + throwable.getMessage());
            }

            @Override
            public Result queryTaskNode(AirAlTaskLogReq airAlTaskLogReq) {
                return Result.fail("查询飞行任务接入节点失败:" + throwable.getMessage());
            }

            @Override
            public Result closeAirTaskAndAlgorithms(AirTaskOperateReq airPointToFlyReq) {
                return Result.fail("退订设备后 关闭设备下的航线任务和算法任务:" + throwable.getMessage());
            }

            @Override
            public Result openAirTaskAndAlgorithms(AirTaskOperateReq airPointToFlyReq) {
                return Result.fail("续订设备后 重新开启设备下的航线任务和算法任务:" + throwable.getMessage());
            }

            @Override
            public Result pointToFly(AirPointToFlyReq airPointToFlyReq) {
                return Result.fail("指点飞行:" + throwable.getMessage());
            }

            @Override
            public Result pointToFlyBatch(List<AirPointToFlyReq> airPointToFlyReq) {
                return Result.fail("批量点飞:" + throwable.getMessage());
            }

            @Override
            public Result flyReturn(List<AirPointToFlyReq> airPointToFlyReq) {
                return Result.fail("批量返航:" + throwable.getMessage());
            }

            @Override
            public Result flyReturnBatch(List<AirPointToFlyReq> airPointToFlyReq) {
                return Result.fail("批量一键返航:" + throwable.getMessage());
            }

            @Override
            public Result getAirLineInfoByFlightRouteId(AirEditDto airEditDto) {
                return Result.fail("获取航线详情:" + throwable.getMessage());
            }

            @Override
            public Result getAirAlarmType(AirAlarmTypeReq alarmTypeDTO) {
                return Result.fail("获取无人机算法配置:" + throwable.getMessage());
            }

            @Override
            public Result editAirLine(AirEditDto airEditDto, MultipartFile kmzFile) {
                return Result.fail("新增/编辑航线:" + throwable.getMessage());
            }

            @Override
            public Result copyAirLine(AirEditDto airEditDto) {
                return Result.fail("复制航线:" + throwable.getMessage());
            }

            @Override
            public Result uploadKmzFile(MultipartFile kmzFile) {
                return Result.fail("上传航线文件:" + throwable.getMessage());
            }

            @Override
            public Result checkAirLineName(AirEditDto airEditDto) {
                return Result.fail("校验航线名称唯一性:" + throwable.getMessage());
            }

            @Override
            public Result checkAirLineBind(AirEditDto airEditDto) {
                return Result.fail("校验航线是否被关联:" + throwable.getMessage());
            }

            @Override
            public Result queryDefultAirLineName(AirEditDto airEditDto) {
                return Result.fail("查询默认航线名称:" + throwable.getMessage());
            }

            @Override
            public Result getAirLineInfo(AirEditDto airEditDto) {
                return Result.fail("获取航线详情:" + throwable.getMessage());
            }

            @Override
            public Result editAirLineName(AirEditDto airEditDto) {
                return Result.fail("修改航线名称:" + throwable.getMessage());
            }

            @Override
            public Result delAirLine(AirEditDto airEditDto) {
                return Result.fail("删除航线:" + throwable.getMessage());
            }

            @Override
            public Result addTempAlgorithm(AirEditDto airEditDto) {
                return Result.fail("航线新增临时算法:" + throwable.getMessage());
            }

            @Override
            public Result queryAirLineAlgorithm(AirEditDto airEditDto) {
                return Result.fail("查询航线算法:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo getAirLineList(AirEditDto airEditDto) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }


            @Override
            public Result getAirLinesList(AirEditDto airEditDto) {
                return Result.fail("查询租户行业下航线下拉列表sdk:" + throwable.getMessage());
            }

            /**
             * 航线列表
             *
             * @param airEditDto
             * @return
             */
            @Override
            public Result getAirLinesListSdk(AirEditDto airEditDto) {
                return null;
            }


            @Override
            public Result addAirLineCollect(AirEditDto airEditDto) {
                return Result.fail("添加航线收藏:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo queryAirLineCollectList(AirEditDto airEditDto) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result deleteAirLineCollect(AirEditDto airEditDto) {
                return Result.fail("取消航线收藏:" + throwable.getMessage());
            }

            @Override
            public Result getAirDeviceInfo(AirDeviceInfoReq airDeviceInfoDto) {
                return Result.fail("获取设备详细信息:" + throwable.getMessage());
            }

            @Override
            public Result getPointInfo(AirEditDto airEditDto) {
                return Result.fail("获取航点航线详细信息:" + throwable.getMessage());
            }

            @Override
            public Result immediatelyFly(AirPointToFlyReq airPointToFlyReq) {
                return Result.fail("一键起飞:" + throwable.getMessage());
            }

            @Override
            public Result reFly(AirEditDto airEditDto, MultipartFile kmzFile) {
                return Result.fail("一键复飞:" + throwable.getMessage());
            }

            @Override
            public Result reFlyApp(AirEditDto airEditDto) {
                return Result.fail("APP一键复飞:" + throwable.getMessage());
            }

            @Override
            public Result getAirLineByDevics(AirEditDto airEditDto) {
                return Result.fail("查询设备下的航线:" + throwable.getMessage());
            }

            @Override
            public Result getVideoRealtimeUrl(AirVideoReq airVideoReq) {
                return Result.fail("获取无人机视频实时流地址:" + throwable.getMessage());
            }

            @Override
            public Result switchLiveStream(AirVideoReq airVideoReq) {
                return Result.fail("切换直播流清晰度:" + throwable.getMessage());
            }

            @Override
            public Result queryHisRecordLimit(AirHistoryReq airHistoryReq) {
                return null;
            }

            @Override
            public TableDataInfo queryTaskListLimit(AirTaskListReq airTaskReq) {
                return null;
            }

            @Override
            public TableDataInfo queryTaskPhotos(AirResultTaskReq airResultTaskReq) {
                return null;
            }

            /**
             * 根据任务获取图片集合(行业sdk，需要传租户行业编码)
             *
             * @param airResultTaskReq
             * @return
             */
            @Override
            public TableDataInfo queryTaskPhotosSdk(AirResultTaskReq airResultTaskReq) {
                return null;
            }

            @Override
            public TableDataInfo queryTaskVideos(AirResultTaskReq airResultTaskReq) {
                return null;
            }

            @Override
            public Result photoVideoDelete(AirResultReq airResultReq) {
                return Result.fail("批量删除图片/视频成果物:" + throwable.getMessage());
            }

            @Override
            public Result photoVideoRename(AirResultRenameReq airResultRenameReq) {
                return Result.fail("重命名图片/视频成果物:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo queryTaskList(AirResultTaskReq airResultTaskReq) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            /**
             * 获取飞行结果任务列表(行业sdk,入参需要传租户和行业编码)
             *
             * @param airResultTaskReq
             * @return
             */
            @Override
            public TableDataInfo queryTaskListSdk(AirResultTaskReq airResultTaskReq) {
                return null;
            }

            @Override
            public Result queryHisRecordByVideoTime(AirResultTaskReq airResultTaskReq) {
                return Result.fail("通过视频时间查询轨迹:" + throwable.getMessage());
            }

            @Override
            public Result queryHisRecords(AirHistoryReq airHistoryReq) {
                return Result.fail("查询历史轨迹:" + throwable.getMessage());
            }

            @Override
            public Result queryHisRecordsBySelf(AirHistoryReq airHistoryReq) {
                return Result.fail("查询历史轨迹、视频融合:" + throwable.getMessage());
            }

            @Override
            public Result mediaSync(AirResultTaskReq airResultTaskReq) {
                return Result.fail("同步数据源:" + throwable.getMessage());
            }

            @Override
            public Result queryDefultUav() {
                return Result.fail("获取无人机树默认最新任务设备:" + throwable.getMessage());
            }

            @Override
            public Result doVideoExtraction(AirVideoExtractReq videoExtractReq) {
                return Result.fail("视频抽帧:" + throwable.getMessage());
            }

            @Override
            public Result queryVideoExtractionList(AirVideoExtractReq videoExtractReq) {
                return Result.fail("查询抽帧列表:" + throwable.getMessage());
            }

            @Override
            public Result queryExtractionProgress(AirVideoExtractReq extractReq) {
                return Result.fail("查询抽帧进度:" + throwable.getMessage());
            }

            @Override
            public Result delExtractTaskPhotos(AirExtractTaskPhotoReq extractTaskPhotoReq) {
                return Result.fail("删除抽帧任务图片:" + throwable.getMessage());
            }

            @Override
            public Result cancelExtractTask(AirVideoExtractTaskReq extractTaskReq) {
                return Result.fail("取消抽帧:" + throwable.getMessage());
            }

            @Override
            public Result addCollectResult(AirResultReq airResultReq) {
                return Result.fail("收藏飞行结果:" + throwable.getMessage());
            }

            @Override
            public Result delCollectResult(AirResultReq airResultReq) {
                return Result.fail("取消收藏飞行结果:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo queryCollectResultList(AirResultReq airResultReq) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result delCollectResultById(AirResultReq airResultReq) {
                return Result.fail("删除收藏成果:" + throwable.getMessage());
            }

            @Override
            public Result secretJson(AirResultReq airResultReq) {
                return Result.fail("收藏链接分享加解密:" + throwable.getMessage());
            }

            @Override
            public Result queryPhotoVideoAttr(AirResultReq airResultReq) {
                return Result.fail("查询照片/视频的详情:" + throwable.getMessage());
            }

            @Override
            public Result queryReFlightTask(AirResultReq airResultReq) {
                return Result.fail("查询复飞任务结果集失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo queryTaskPhotoVideoInfo(QueryTaskPhotoVideoReq airResultReq) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result queryCorrespondPhotoList(CorrespondPointReq correspondPointReq) {
                return Result.fail("查询图片列表失败:" + throwable.getMessage());
            }

            @Override
            public Result queryFlightTaskList(AirResultReq airResultReq) {
                return Result.fail("查询飞行任务下拉列表失败:" + throwable.getMessage());
            }

            /**
             * 查询用户订购无人机信息
             *
             * @param telemetryReq
             */
            @Override
            public Result queryUserDeviceOrder(TelemetryReq telemetryReq) {
                return Result.fail("查询用户订购无人机信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<AirLineVo> queryAirLineByTaskId(AirTaskReq airTaskReq) {
                return Result.fail("根据任务id查询航线失败:" + throwable.getMessage());
            }
        };
    }
}
