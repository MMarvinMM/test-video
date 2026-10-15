package com.chinatower.event.core.alarm.strategy;

import com.chinatower.event.core.alarm.AlarmStrategyEnum;
import com.chinatower.event.core.alarm.handler.AbstractHandler;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Description 处理卫星告警
 * @Author guyuefei
 * @Date 2022/7/7
 * @Version release_v1.5
 **/
@Component("satelliteAlarmStrategy")
@Slf4j
public class SatelliteAlarmStrategy  extends AlarmBaseStategy {
    @Override
    public boolean jdugeAlarmType(ReceiveAlarmVO receiveAlarmVO) {
        return false;
    }

    /**
     * 执行特有处理器逻辑
     * @return
     */
    @Override
    public AbstractHandler specialHandle() {
        return null;
    }

    @Override
    public String getStrategyName(){
        return AlarmStrategyEnum.SATELLITE_ALARM.getAlarmStrategyBean();
    }
}
