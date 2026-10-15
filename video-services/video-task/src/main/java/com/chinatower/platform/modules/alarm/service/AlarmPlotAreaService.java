package com.chinatower.platform.modules.alarm.service;

import com.chinatower.analysis.api.entity.DateVO;
import com.chinatower.platform.modules.alarm.entity.AlarmPlotAreaDto;

import java.util.List;

public interface AlarmPlotAreaService {

    /**
     * 查询告警地块面积
     * @return
     */
    List<AlarmPlotAreaDto> getAlarmPlotAreaList(DateVO dateVO);

    /**
     * 插入告警地块面积沉淀表
     * @param list
     * @return
     */
    boolean insertAlarmPlotArea(DateVO dateVO, List<AlarmPlotAreaDto> list);
}
