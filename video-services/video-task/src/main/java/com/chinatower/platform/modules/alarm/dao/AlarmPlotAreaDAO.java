package com.chinatower.platform.modules.alarm.dao;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.analysis.api.entity.DateVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.modules.alarm.entity.AlarmPlotAreaDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AlarmPlotAreaDAO {

    /**
     * 获取当天告警事件的所有taskId
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map<String, Object>> getTaskIdForAlarmTime(@Param("param") Map<String, Object> param);

    /**
     * 根据taskId集合获取图层id、图斑id
     * @param taskIdList
     * @return
     */
    @DS("base")
    List<Map<String, Object>> getLayerPolygon(List<String> taskIdList);

    /**
     * 新增告警地块面积
     * @param list
     * @return
     */
    @DS("statistic")
    int addAlarmPlotArea(List<AlarmPlotAreaDto> list);

    /**
     * 删除当天的告警地块面积
     * @param dateVO
     * @return
     */
    @DS("statistic")
    int deleteAlarmPlotArea(DateVO dateVO);
}
