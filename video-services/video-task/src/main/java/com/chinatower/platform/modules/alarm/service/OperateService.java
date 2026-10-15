package com.chinatower.platform.modules.alarm.service;

import com.chinatower.analysis.api.entity.AlarmOrderJudgeVO;
import com.chinatower.analysis.api.entity.DateVO;

import java.util.List;

/**
 * @className: OperateService
 * @description: 运营研判service
 */
public interface OperateService {

    /**
     * 运营研判数量统计
     * @param dateVO
     * @return
     */
    List<AlarmOrderJudgeVO> getOperateNumList(DateVO dateVO);

    /**
     * 根据入参进行运营研判数量统计
     * @param dateVO
     * @return
     */
    List<AlarmOrderJudgeVO> getOperateNumListParam(DateVO dateVO);
}
