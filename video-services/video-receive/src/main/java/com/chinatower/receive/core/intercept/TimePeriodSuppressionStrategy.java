package com.chinatower.receive.core.intercept;

import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.vo.AlgorithmEventVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.util.DateUtils;
import com.chinatower.receive.core.util.StrategyTypeEnum;
import com.chinatower.receive.vo.entity.VmAlarmSuppressStrategyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * @className: TimePeriodSuppressionStrategy
 * @description: 压制方式-压制时间、时间段策略实现类
 * @author: lyy
 * @date: 2024/08/01 15:35
 */
@Component("timePeriodStrategy")
@Slf4j
public class TimePeriodSuppressionStrategy extends BaseAlarmSuppressStategy {

    @Override
    public boolean apply(ReceiveAlarmVO receiveAlarmVO, VmAlarmSuppressStrategyVO strategy) {
        boolean suppressFlag = false;
        // 时间段判断处理
        int alarmHour = Integer.parseInt(receiveAlarmVO.getAlarmTime().substring(Constants.ELEVEN_NUM, Constants.THIRTEEN_NUM));
        //获取到告警时间（yyyy-MM-dd格式）
        Date alarmTime = DateUtils.stringToDateYMDHMS(receiveAlarmVO.getAlarmTime().substring(Constants.ZERO_NUM, Constants.TEN_NUM));
        if(!StringUtils.isEmpty(strategy.getSuppressPeriodTime()) && Arrays.stream(strategy.getSuppressPeriodTime().split(Constants.COMMA)).map(Integer::parseInt).collect(Collectors.toList()).contains(alarmHour)){
            if(StringUtils.isEmpty(strategy.getSuppressStartTime()) || ((alarmTime.after(strategy.getSuppressStartTime()) || alarmTime.equals(strategy.getSuppressStartTime())) && (alarmTime.before(strategy.getSuppressEndTime()) || alarmTime.equals(strategy.getSuppressEndTime())))){
                receiveAlarmVO.setAlgorithmTime(strategy.getSuppressPeriodTime());
                receiveAlarmVO.setIntercept(AlgorithmEventVO.FOR_ALGORITHM_TIME);
                suppressFlag = true;
                log.info("告警编码:{},时间段告警压制",receiveAlarmVO.getAlarmCode());
            }
        }
        return suppressFlag;
    }

    @Override
    public int getOrder() {
        return Constants.ONE_NUM;
    }

    @Override
    public boolean getStrategyBeanName(String stategyType) {
        if (stategyType.equals(StrategyTypeEnum.WX_TIME_PERIOD_STRATEGY.getDisplayName())) {
            return StrategyTypeEnum.WX_TIME_PERIOD_STRATEGY.getDisplayName().equals(stategyType);
        }
        return StrategyTypeEnum.TIME_PERIOD_STRATEGY.getDisplayName().equals(stategyType);
    }
}
