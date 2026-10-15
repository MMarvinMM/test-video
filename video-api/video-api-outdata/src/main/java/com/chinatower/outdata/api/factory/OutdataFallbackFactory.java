package com.chinatower.outdata.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.outdata.api.OutdataAPI;
import com.chinatower.outdata.api.entity.*;
import com.github.pagehelper.PageInfo;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 视频控制降级处理
 *
 * @Author: zhujun
 * @Date: 2022/1/16
 */
@Component
public class OutdataFallbackFactory implements FallbackFactory<OutdataAPI> {
    private static final Logger log = LoggerFactory.getLogger(OutdataFallbackFactory.class);

    @Override
    public OutdataAPI create(Throwable throwable) {
        log.error("调用传感器接口失败:{}", throwable.getMessage());
        return new OutdataAPI() {
            @Override
            public Result queryStatus(String deviceCodes) {
                return Result.fail("调用查询传感器状态方法失败:" + throwable.getMessage());
            }

            @Override
            public Result queryAttribute(String deviceCodes) {
                return Result.fail("调用查询传感器参数方法失败:" + throwable.getMessage());
            }

            @Override
            public Result queryAttributeMap(String deviceCodes) {
                return Result.fail("调用查询传感器参数方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<MonitorFake>> queryStatusFake(QueryStatusFakeReq queryStatusFakeReq) {
                return Result.fail("调用传感器状态站址信息查询方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<PageInfo<MonitorFake>> queryStatusFakePage(QueryStatusFakeReq queryStatusFakeReq) {
                return Result.fail("调用传感器状态站址信息查询方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<Integer> querySensorCount(QuerySensorCountReq querySensorCountReq) {
                return Result.fail("传感器数量查询方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<EcmwfInfoParam> getEcmwf(@RequestBody EcmwfVO vo) {
                return Result.fail("调用获取气象查询方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<RelayDeviceInfo> queryRelayRadarList(RadarDeviceStatusVO dto) {
                return Result.fail("获取正在接力的设备失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<RadarReviewInfo>> queryRadarReviewInfoList(RadarTrackDTO dto) {
                return Result.fail("目标回放数据查询失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<RadarTrackInfo>> queryRadarTrackInfoList(RadarTrackDTO dto) {
                return Result.fail("目标历史轨迹查询失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceAccessNodeVO>> queryDeviceAccessNodeInfoList(DeviceAccessNodeDTO dto) {
                return Result.fail("摄像机接入节点信息查询:" + throwable.getMessage());
            }

            @Override
            public Result<List<WarningTargetVO>> queryWarningTargetInfoList() {
                return Result.fail("雷达告警目标信息查询:" + throwable.getMessage());
            }

            @Override
            public Result<List<RadarReviewInfo>> queryAisSearch(RadarTrackDTO dto) {
                return Result.fail("目雷达-AIS目标搜索查询失败:" + throwable.getMessage());
            }

            @Override
            public Result<AisDetailsInfo> queryAisDetails(RadarTrackDTO dto) {
                return Result.fail("雷达-目标信息查询查询失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<TrackAllInfo>> queryTrajectoryTrack(RadarTrackDTO dto) {
                return Result.fail("雷达-轨迹查询查询失败:" + throwable.getMessage());
            }

            @Override
            public Result<TrackAllInfo> queryTrajectoryTrackPoint(RadarTrackDTO dto) {
                return Result.fail("雷达-轨迹点信息查询失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<TrackAllInfo>> trackPointChartQuery(List<RadarTrackDTO> dto) {
                return Result.fail("雷达-轨迹分析查询失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<ThermalMapInfo>> queryTrajectoryThermalMap(RadarTrackDTO dto) {
                return Result.fail("雷达-热力图查询失败:" + throwable.getMessage());
            }

            @Override
            public Result<FixedTargetRecord> queryFixedTarget(FixedTargetDTO dto) {
                return Result.fail("获取固定目标列表失败:" + throwable.getMessage());
            }

            @Override
            public Result<UpdateFixedTargetIdList> updateFixedTarget(FixedTargetDTO dto) {
                return Result.fail("修改固定目标状态失败:" + throwable.getMessage());
            }

            @Override
            public Result<GlobalTrackTrackMap> globalTrack(GlobalTrackDTO dto) {
                return Result.fail("获取全局轨迹失败:" + throwable.getMessage());
            }

            @Override
            public Result<byte[]> getTile(String node, String userName, Integer level, Integer col, Integer row, String skipCode) {
                return Result.fail("获取海图瓦片接口失败:" + throwable.getMessage());
            }

            @Override
            public Result<byte[]> getExtendTile(String tileType, String userName, Integer level, Integer col, Integer row) {
                return Result.fail("获取渔区瓦片接口失败:" + throwable.getMessage());
            }
        };
    }
}
