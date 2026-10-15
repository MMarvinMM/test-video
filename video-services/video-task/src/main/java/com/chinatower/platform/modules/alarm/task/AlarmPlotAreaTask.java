package com.chinatower.platform.modules.alarm.task;

import cn.hutool.core.date.DateUtil;
import com.chinatower.analysis.api.entity.DateVO;
import com.chinatower.common.core.utils.TableUtil;
import com.chinatower.platform.common.Constants;
import com.chinatower.platform.modules.alarm.entity.AlarmPlotAreaDto;
import com.chinatower.platform.modules.alarm.service.AlarmPlotAreaService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;


/**
 * @desription: 国土sdk：获取告警地块面积
 * @data 2023/08/09 14:30
 * @author lyy
 * @version v1.6.10
 *
 */
@Component
@Slf4j
public class AlarmPlotAreaTask {

    @Autowired
    private AlarmPlotAreaService alarmPlotAreaService;

    /**
     * 定时任务：处理告警地块面积数据。
     * 此方法会获取当天的告警数据表名，根据当天日期查询告警地块面积数据列表，
     * 如果查询到数据，则插入到目标表中。
     *
     * @param param 任务执行参数（未使用）
     * @return 返回任务执行结果，成功返回 ReturnT.SUCCESS
     */
    @XxlJob("alarmPlotAreaHandler")
    public ReturnT<String> alarmPlotAreaHandler(String param) {
        long startTime = System.currentTimeMillis();
        log.info("alarmPlotAreaHandler-开始时间:{}",startTime);
        XxlJobLogger.log("获取告警地块面积定时任务alarmPlotAreaHandler开始执行,开始时间为:{}", startTime);

        // 获取当天的日期和告警数据表名
        Date date = new Date();
        String tableName = TableUtil.getTableName(date, Constants.WARN_ORDER_TEBLE);
        // 获取当天日期转字符串
        String dateString = DateUtil.format(date, Constants.YYYY_MM_DD);
        // 构建查询条件对象（包含表名和日期）
        DateVO dateVO = new DateVO();
        dateVO.setTableName(tableName);
        dateVO.setHandleDate(dateString);
        // 查询当天的告警地块面积数据
        List<AlarmPlotAreaDto> list = alarmPlotAreaService.getAlarmPlotAreaList(dateVO);
        // 如果查询结果不为空，插入告警地块面积数据
        if(!CollectionUtils.isEmpty(list)){
            alarmPlotAreaService.insertAlarmPlotArea(dateVO, list);
        }

        long endTime = System.currentTimeMillis();
        log.info("alarmPlotAreaHandler-结束时间:{}",endTime - startTime);
        XxlJobLogger.log("获取告警地块面积定时任务alarmPlotAreaHandler执行完成,耗时:{}", endTime - startTime);
        return ReturnT.SUCCESS;
    }
}
