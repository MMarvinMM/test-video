package com.chinatower.event.repository;

import com.chinatower.event.dto.AnalysisDTO;
import com.chinatower.event.dto.EventNumByAreaDTO;
import com.chinatower.event.dto.EventNumByHourDTO;
import com.chinatower.event.dto.EventNumNewDTO;
import com.chinatower.event.entity.vo.AreaEventVO;

import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * 统计库查询数据
 * <p>
 * 类名称: IStatisticsRepository.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/14 11:11
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
public interface IStatisticsRepository {
    
    /**
     * 获取事件统计数量
     *
     * @return
     * @author chenmf
     * @date 2022/6/7
     * @version
     */
    EventNumNewDTO getEventNumNew(AnalysisDTO analysisDTO);
    
    /**
     * 告警事件统计-事件高发区域
     *
     * @return
     * @author chenmf
     * @date 2022/6/7
     * @version
     */
    List<EventNumByAreaDTO> getEventNumByArea(AnalysisDTO analysisDTO);
    
    /**
     * 获取事件数量-曲线图用
     *
     * @return
     * @author chenmf
     * @date 2022/6/7
     * @version
     */
    List<EventNumByHourDTO> getEventNumByHour(AnalysisDTO analysisDTO);
    
    /**
     * 告警事件统计-事件高发区域全景TOP5
     *
     * @return
     * @author chenmf
     * @date 2022/6/7
     * @version
     */
    List<AreaEventVO> getEventNumListByArea(AnalysisDTO analysisDTO);
}
