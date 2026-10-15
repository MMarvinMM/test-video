package com.chinatower.video.api.factory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.chinatower.video.api.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.chinatower.common.entity.Result;
import com.chinatower.video.api.deprecated.VideoQueryAPIMigrate;

import feign.hystrix.FallbackFactory;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * 查询功能降级处理
 *
 * @Author: zhujun
 * @Date: 2022/1/16
 */
@Component
public class VideoQueryFallbackFactory implements FallbackFactory<VideoQueryAPIMigrate> {
private static final Logger log = LoggerFactory.getLogger(VideoQueryFallbackFactory.class);

    @Override
    public VideoQueryAPIMigrate create(Throwable throwable) {
        log.error("调用视频播放接口失败:{}", throwable.getMessage());
        return new VideoQueryAPIMigrate() {

            @Override
            public Result selectRoleById(QueryRoleReq queryRoleReq) {
                return Result.fail("调用查询通道的录像列表方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<QueryRecordPlanData>> queryRecordPlan(VideoBaseReq videoBaseReq) {
                return Result.fail("调用查询通道录像计划方法失败:" + throwable.getMessage());
            }

            @Override
            public Result queryRecordPlanForPage(VideoChannelVideoPlanVO channelVideoPlanVO) {
                return Result.fail("调用查询全部录像计划方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<PresetData> queryPtzPosition(VideoBaseReq videoBaseReq) {
                return Result.fail("调用查询PTZ位置方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<QueryPresetListData>> queryPresetList(VideoBaseReq videoBaseReq) {
                return Result.fail("调用查询预置点列表方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<QueryGuardListData> queryGuardList(VideoBaseReq videoBaseReq) {
                return Result.fail("调用查询看守位方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<QueryPatrolData>> queryPatrol(VideoQueryPatrolData videoQueryPatrolData) {
                return Result.fail("调用查询巡航路径方法失败:" + throwable.getMessage());
            }

            @Override
            public Result getEnumInfo(QueryGetEnumInfoReq queryGetEnumInfoReq) {
                return Result.fail("调用获取视频格式字典表方法失败:" + throwable.getMessage());
            }

			@Override
			public Result<PresetData> turnToPtzByPointAngle2(VideoPtzQueryRequest videoPtzQueryReq) {
				return Result.fail("调用根据定位点转动Ptz方法失败:" + throwable.getMessage());
			}

            @Override
            public Result getVideoRealtimeUrlsForLookHere(@RequestBody List< Map > list) {
                return Result.fail("批量获取通道实时流URL地址 看这里sdk用:" + throwable.getMessage());
            }
            @Override
            public Result<Set<PlayerGetVideoRealtimeUrlData>> getVideoRealtimeUrlsForComponent(@RequestBody Set<PlayerGetVideoRealtimeUrlData> reqList) {
                return Result.fail("全域感知大屏，圈选摄像机，批量拉取实时流:" + throwable.getMessage());
            }
        };
    }
}
