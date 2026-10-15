package com.chinatower.event.core.alarm.strategy;

import com.chinatower.event.core.alarm.AlarmStrategyEnum;
import com.chinatower.event.core.alarm.handler.AbstractHandler;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Description 人工上报
 * @Author guyuefei
 * @Date 2022/7/7
 * @Version release_v1.5
 **/
@Component("humanAlarmStrategy")
@Slf4j
public class HumanAlarmStrategy extends AlarmBaseStategy {

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
        return AlarmStrategyEnum.ARTIFICIAL.getAlarmStrategyBean();
    }
}
