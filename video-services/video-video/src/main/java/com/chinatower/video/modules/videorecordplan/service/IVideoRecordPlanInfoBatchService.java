package com.chinatower.video.modules.videorecordplan.service;

import java.util.List;
import java.util.Map;

import com.chinatower.video.api.entity.ChannelVideoPlanRequestVO;
import com.chinatower.video.api.entity.SttPatrolReq;


/**
 * @ClassName OrganDeviceRelService
 * @Deacription 视频通道录像计划服务类
 * @Author chenmf
 * @Date 2022/2/19 15:27
 * @Version 1.0
 **/
public interface IVideoRecordPlanInfoBatchService {
    
    /**
     * 批量停用/启用通道录像计划
     *
     * @param channelVideoPlanRequestVO
     * @return
     */
    void batchStartEndRecordPlan(ChannelVideoPlanRequestVO channelVideoPlanRequestVO);
    
    /**
     * 批量停用/启用巡航计划
     * @param patrolDTOList
     */
    void batchStartEndPatrol(List<SttPatrolReq> patrolDTOList);

    /**
     * 批量停用/启用巡航计划
     * @param patrolDTOList
     */
    List<Map<String, String>> batchDoPatrol(List<SttPatrolReq> patrolDTOList);
}
