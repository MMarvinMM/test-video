package com.chinatower.platform.modules.statistics.service;

import com.chinatower.analysis.api.entity.DateVO;
import com.chinatower.analysis.api.entity.JudgeVO;

import java.util.List;

/**
 * @className: StatisticsAlarmService
 * @description: StatisticsAlarmService
 * @author: yongqi.han
 * @date: 2022/6/20
 * @requestID 需求单号及需求名称
 */
public interface StatisticsAlarmService {

    /**
     * @description: 查询运营研判统计数量信息
     * @author: yongqi.han
     * @date: 2022/6/20
     * @param dateVO
     * @return: List<Map>
     */
    List<JudgeVO> getEventJudgeAlarm(DateVO dateVO);
    List<JudgeVO> getEventJudgeAlarmParam(DateVO dateVO);

    /**
     * @description: 批量新增运营研判数据
     * @author: yongqi.han
     * @date: 2022/6/20
     * @param dateVO
     * @param list
     * @return: int
     */
    void insertEventJudgeAlarm(DateVO dateVO, List<JudgeVO> list);
    int insertEventJudgeAlarmParam(DateVO dateVO, List<JudgeVO> list);

}
