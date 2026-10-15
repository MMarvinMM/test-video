package com.chinatower.video.api.deprecated;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.api.factory.VideoQueryFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 不对外暴露此文件的sdk接口，改为对外暴露同名class
 * gitVersion:  1.6.90
 * date:    2024年8月12日
 */
@FeignClient(contextId = "VideoQueryAPI",
        value = ServiceNameConstants.VIDEO_SERVICE,
        fallbackFactory = VideoQueryFallbackFactory.class)
@Deprecated
public interface VideoQueryAPIMigrate {

    @PostMapping("/video/query/sdk/selectRoleById")
    Result selectRoleById(@RequestBody QueryRoleReq queryRoleReq);

    @PostMapping("/video/query/sdk/queryRecordPlan")
    Result<List<QueryRecordPlanData>> queryRecordPlan(@RequestBody VideoBaseReq videoBaseReq);

    @PostMapping("/video/query/sdk/queryRecordPlanForPage")
    Result queryRecordPlanForPage(@RequestBody VideoChannelVideoPlanVO channelVideoPlanVO);

    @PostMapping("/video/query/sdk/queryPtzPosition")
    Result<PresetData> queryPtzPosition(@RequestBody VideoBaseReq videoBaseReq);

    @PostMapping("/video/query/sdk/queryPresetList")
    Result<List<QueryPresetListData>> queryPresetList(@RequestBody VideoBaseReq videoBaseReq);

    @PostMapping("/video/query/sdk/queryGuardList")
    Result<QueryGuardListData> queryGuardList(@RequestBody VideoBaseReq videoBaseReq);

    @PostMapping("/video/query/sdk/queryPatrol")
    Result<List<QueryPatrolData>> queryPatrol(@RequestBody VideoQueryPatrolData videoQueryPatrol);


    @PostMapping("/video/query/sdk/getEnumInfo")
    Result getEnumInfo(@RequestBody QueryGetEnumInfoReq queryGetEnumInfoReq);


    @PostMapping("/video/query/turnToPtzByPointAngle2")
    Result<PresetData> turnToPtzByPointAngle2(@RequestBody VideoPtzQueryRequest videoPtzQueryReq);

    @Deprecated
    @PostMapping("/devices/channels/getVideoRealtimeUrlsForLookHere")
    Result getVideoRealtimeUrlsForLookHere(@RequestBody List<Map> list);

    /**
     * 全域感知大屏，圈选摄像机，批量拉取实时流
     * @param reqList
     * @return
     */
    @Deprecated
    @PostMapping("/devices/channels/getVideoRealtimeUrlsForComponent")
    Result<Set<PlayerGetVideoRealtimeUrlData>> getVideoRealtimeUrlsForComponent(@RequestBody Set<PlayerGetVideoRealtimeUrlData> reqList);
}
