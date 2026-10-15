package com.chinatower.event.service;

import com.chinatower.common.core.utils.TableUtil;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.dto.StatisticsDTO;
import com.chinatower.event.dto.TodayAlarmRealtimeQueryDTO;
import com.chinatower.event.entity.vo.TodayAlarmRealtimeStatVO;
import com.chinatower.event.mapper.StatisticsMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 今日告警实时统计
 */
@Service
public class TodayAlarmStatService {

    @Resource
    private StatisticsMapper statisticsMapper;

    /**
     * 实时统计今日告警、今日待研判、今日真警（alarm_time 均为今日）
     */
    public TodayAlarmRealtimeStatVO queryTodayAlarmStatRealtime(StatisticsDTO statisticsDTO) {
        TodayAlarmRealtimeQueryDTO query = new TodayAlarmRealtimeQueryDTO();
        if (statisticsDTO != null) {
            BeanUtils.copyProperties(statisticsDTO, query);
        }
        query.setTableName(TableUtil.getTableName(new Date(), TableConstant.WARN_ORDER_TEBLE));

        TodayAlarmRealtimeStatVO stat = statisticsMapper.countTodayAlarmStatRealtime(query);
        if (stat == null) {
            stat = new TodayAlarmRealtimeStatVO();
        }
        if (stat.getTodayAll() == null) {
            stat.setTodayAll(0);
        }
        if (stat.getPenddingJudged() == null) {
            stat.setPenddingJudged(0);
        }
        if (stat.getRealNum() == null) {
            stat.setRealNum(0);
        }
        return stat;
    }
}
