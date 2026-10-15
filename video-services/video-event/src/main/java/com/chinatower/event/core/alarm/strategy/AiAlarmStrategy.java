package com.chinatower.event.core.alarm.strategy;

import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.core.alarm.AlarmStrategyEnum;
import com.chinatower.event.core.alarm.handler.AISpecialHandler;
import com.chinatower.event.core.alarm.handler.AbstractHandler;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @Description AI告警
 * @Author guyuefei
 * @Date 2022/7/7
 * @Version release_v1.5
 **/
@Component("aiAlarmStrategy")
@Slf4j
public class AiAlarmStrategy extends AlarmBaseStategy {
    @Autowired
    private AISpecialHandler aiSpecialHandler;

    @Override
    public boolean jdugeAlarmType(ReceiveAlarmVO receiveAlarmVO) {
        boolean flag =  StringUtils.isEmpty(receiveAlarmVO.getAlarmSource()) || WarningSourceEnum.AI_ALARM.getTypeValue().equals(receiveAlarmVO.getAlarmSource())
        || WarningSourceEnum.THIRD_ALARM.getTypeValue().equals(receiveAlarmVO.getAlarmSource());
        if (flag) {
            //如果AlarmSource为空，默认为AI告警
            receiveAlarmVO.setAlarmSource(Constants.WARNING_SOURCE_1);
        }
        return flag;
    }

    /**
     * 执行特有处理器逻辑
     * @return
     */
    @Override
    public AbstractHandler specialHandle() {
        return aiSpecialHandler;
    }

    @Override
    public String getStrategyName(){
        return AlarmStrategyEnum.AI_ALARM.getAlarmStrategyBean();
    }
}
