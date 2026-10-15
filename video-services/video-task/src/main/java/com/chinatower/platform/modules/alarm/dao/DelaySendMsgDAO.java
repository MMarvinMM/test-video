package com.chinatower.platform.modules.alarm.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.modules.alarm.entity.delaysendmsg.DelayFlowVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @className: JudgeStatisticsMapper
 * @description: 研判统计
 * @author: yongqi.han
 * @date: 2022/9/5
 * @requestID 需求单号及需求名称
 */
@Mapper
public interface DelaySendMsgDAO {

    /**
     * 查询所有配置为稍后提醒的流程环节（自定义加行业默认）
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<DelayFlowVo> getDealyFlowList();



}
