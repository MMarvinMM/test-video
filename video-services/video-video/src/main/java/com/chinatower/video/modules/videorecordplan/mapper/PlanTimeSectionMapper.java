package com.chinatower.video.modules.videorecordplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.video.api.entity.ChannelVideoPlanVO;
import com.chinatower.video.modules.videorecordplan.dataobject.PlanTimeSectionDO;

/**
 * <p>
 * 通道录像计划时间段表 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-19
 */
public interface PlanTimeSectionMapper extends BaseMapper<PlanTimeSectionDO> {

    int deletePlanTimeSection(ChannelVideoPlanVO channelVideoPlanVO);

}
