package com.chinatower.video.modules.videorecordplan.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.video.api.entity.ChannelVideoPlanVO;
import com.chinatower.video.modules.videorecordplan.dataobject.PlanTimeSectionDO;

/**
 * <p>
 * 通道录像计划时间段表 服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-19
 */
public interface IPlanTimeSectionRepository extends IService<PlanTimeSectionDO> {
    
    /**
     * 功能描述 根据RecordPlanId删除通道录像计划时间段表
     * @author chenmf
     * @date 2022/1/24
     * [recordPlanId]
     * @return
     */
    void deleteByRecordPlanId(ChannelVideoPlanVO channelVideoPlanVO);
}
