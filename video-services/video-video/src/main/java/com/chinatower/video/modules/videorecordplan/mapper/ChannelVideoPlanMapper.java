package com.chinatower.video.modules.videorecordplan.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.BatchDeleteChannelVideoPlanVO;
import com.chinatower.video.api.entity.BatchStartOrEndRecordPlanVO;
import com.chinatower.video.api.entity.ChannelVideoPlanVO;
import com.chinatower.video.api.entity.TimeSectionVO;
import com.chinatower.video.modules.videorecordplan.dataobject.ChannelVideoPlanDO;
import com.chinatower.video.modules.videorecordplan.dataobject.PlanTimeSectionDO;
import com.chinatower.video.modules.videorecordplan.dto.ChannelVideoPlanDTO;
import com.chinatower.video.modules.videorecordplan.dto.VideoPlanDateVO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 通道录像计划表 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-19
 */
public interface ChannelVideoPlanMapper extends BaseMapper<ChannelVideoPlanDO> {

    List<ChannelVideoPlanVO> getChannelVideoPlanList(ChannelVideoPlanVO channelVideoPlanVO);

    List<ChannelVideoPlanVO> getChannelVideoPlanMergeList(ChannelVideoPlanVO channelVideoPlanVO);

    ChannelVideoPlanVO getMaxChannelVideoPlan(ChannelVideoPlanVO channelVideoPlanVO);

    List<TimeSectionVO> getTimeSections(ChannelVideoPlanVO channelVideoPlanVO);

    List<TimeSectionVO> getAllTimeSections(ChannelVideoPlanVO channelVideoPlanVO);

    List<TimeSectionVO> getTimeSectionsList(ChannelVideoPlanVO channelVideoPlanVO);

    int saveChannelVideoPlanMerge(ChannelVideoPlanDO vo);

    int saveTimeSectionsMerge(PlanTimeSectionDO vo);

    int saveTimeSectionsMergeBatch(List<PlanTimeSectionDO> list);


    int updateChannelVideoPlanMerge(ChannelVideoPlanVO vo);

    int deleteTimeSections(ChannelVideoPlanVO vo);

    int deleteChannelVideoPlan(ChannelVideoPlanVO vo);
    
    List<ChannelVideoPlanVO> queryRecordPlanMergeParam();

    List<ChannelVideoPlanVO> queryVideoPlanList(BatchDeleteChannelVideoPlanVO vo);

    int batchDelPlan(@Param("list") List<Long> list);

    int batchDelTimeSec(BatchDeleteChannelVideoPlanVO vo);

    int batchUpdatePlan(BatchStartOrEndRecordPlanVO vo);

    List<ChannelVideoPlanDTO> queryRecordPlanDate(VideoPlanDateVO vo);
}
