package com.chinatower.video.modules.videorecordplan.service;

import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.videorecordplan.dto.VideoPlanDateVO;

import java.util.List;
import java.util.Set;


/**
 * @ClassName OrganDeviceRelService
 * @Deacription 视频通道录像计划服务类
 * @Author chenmf
 * @Date 2022/2/19 15:27
 * @Version 1.0
 **/
public interface IVideoRecordPlanInfoService {
    
    /**
     * 新增通道录像计划
     *
     * @param channelVideoPlanRequestVO
     * @return
     */
    void addRecordPlan(ChannelVideoPlanRequestVO channelVideoPlanRequestVO);
    
    /**
     * 修改通道录像计划
     *
     * @param channelVideoPlanVO
     * @return
     */
    void updateRecordPlan(ChannelVideoPlanVO channelVideoPlanVO);
    
    /**
     * 删除通道录像计划
     * @param deleteChannelVideoPlanVO
     * @return
     */
    void deleteRecordPlan(DeleteChannelVideoPlanVO deleteChannelVideoPlanVO);
    
    /**
     * @description: 批量删除通道录像计划
     * @param: channelVideoPlanVO
     * @return:
     */
    Result<String> batchDeleteRecordPlan(BatchDeleteChannelVideoPlanVO batchDeleteChannelVideoPlanVO);
    
    /**
     * @description: 批量停用/启用通道录像计划
     * @param:
     * @return:
     */
    Result<String> batchStartOrEndRecordPlan(BatchStartOrEndRecordPlanVO vo);
    
    /**
     * 查询订单通道录像计划
     *
     * @return
     * @author chenmf
     * @date 2022/2/16
     * @version
     */
    List<ChannelVideoPlanVO> queryOrderRecordPlanInfo(QueryOrderDeviceSetDTO queryOrderDeviceSetDTO);

    void mergeRecordPlan(ChannelVideoPlanVO channelVideoPlanVO) ;

    /**
     * @description: 新增通道录像计划
     * @param:
     * @return:
     */
    void addRecordPlanSingle(ChannelVideoPlanVO channelVideoPlanVO);
    
    void recordPlanMergeParam();

    Result<List<String>> queryRecordPlanDate(VideoPlanDateVO vo);
}
