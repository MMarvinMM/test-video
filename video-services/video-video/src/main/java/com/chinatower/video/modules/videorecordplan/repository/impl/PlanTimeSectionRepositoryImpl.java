package com.chinatower.video.modules.videorecordplan.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.video.api.entity.ChannelVideoPlanVO;
import com.chinatower.video.modules.videorecordplan.dataobject.PlanTimeSectionDO;
import com.chinatower.video.modules.videorecordplan.mapper.PlanTimeSectionMapper;
import com.chinatower.video.modules.videorecordplan.repository.IPlanTimeSectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 通道录像计划时间段表 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-19
 */
@Service
public class PlanTimeSectionRepositoryImpl extends ServiceImpl<PlanTimeSectionMapper, PlanTimeSectionDO> implements IPlanTimeSectionRepository {
    
    @Autowired
    private PlanTimeSectionMapper planTimeSectionMapper;
    
    /**
     * 功能描述 根据RecordPlanId删除通道录像计划时间段表
     * @author chenmf
     * @date 2022/1/24
     * [recordPlanId]
     * @return
     */
    @Override
    public void deleteByRecordPlanId(ChannelVideoPlanVO channelVideoPlanVO) {
        planTimeSectionMapper.deletePlanTimeSection(channelVideoPlanVO);
        
    }
}
