package com.chinatower.receive.core.intercept;

import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.vo.AlarmOriginalAreaVO;
import com.chinatower.event.entity.vo.AlgorithmEventVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.receive.core.util.StrategyTypeEnum;
import com.chinatower.receive.vo.entity.VmAlarmSuppressStrategyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @className: ConfidenceSuppressionStrategy
 * @description: 压制方式-置信度压制策略实现类
 * @author: lyy
 * @date: 2024/08/01 15:35
 */
@Component("confidenceStrategy")
@Slf4j
public class ConfidenceSuppressionStrategy extends BaseAlarmSuppressStategy {

    @Override
    public boolean apply(ReceiveAlarmVO receiveAlarmVO, VmAlarmSuppressStrategyVO strategy) {
        Boolean suppressFlag = false;
        if(strategy.getSuppressThresh() != null){
            suppressFlag = doInterceptByThresh(strategy,receiveAlarmVO);
        }
        return suppressFlag;
    }

    @Override
    public int getOrder() {
        return Constants.TWO_NUM;
    }

    @Override
    public boolean getStrategyBeanName(String stategyType) {
        return StrategyTypeEnum.CONFIDENCE_STRATEGY.getDisplayName().equals(stategyType);
    }

    private boolean doInterceptByThresh(VmAlarmSuppressStrategyVO item, ReceiveAlarmVO receiveAlarmVO) {
        float suppressThresh = item.getSuppressThresh();
        StringBuilder nowThresh = new StringBuilder();
        if (!StringUtils.isEmpty(receiveAlarmVO.getCompConfidenceLevel())) {
            //存在综合置信度，用综合置信度压制
            log.info("告警编码:{},综合置信度:{}.策略中置信度阈值:{}",receiveAlarmVO.getAlarmCode(),receiveAlarmVO.getCompConfidenceLevel(),suppressThresh);
            nowThresh.append(receiveAlarmVO.getCompConfidenceLevel());
            if (Float.parseFloat(receiveAlarmVO.getCompConfidenceLevel()) < suppressThresh) {
                receiveAlarmVO.setThresh(suppressThresh);
                receiveAlarmVO.setIntercept(AlgorithmEventVO.FOR_THRESH);
                receiveAlarmVO.setNowThresh(nowThresh.toString());
                log.info("告警编码:{},综合置信度压制",receiveAlarmVO.getAlarmCode());
                return true;
            }
        } else if(!CollectionUtils.isEmpty(receiveAlarmVO.getAlarmArea())){
            // 不存在综合置信度，用最大置信度压制
            List<AlarmOriginalAreaVO> alarmOriginalAreaVOS = receiveAlarmVO.getAlarmArea();
            boolean flag = true;
            for (AlarmOriginalAreaVO alarmOriginalAreaVO : alarmOriginalAreaVOS) {
                nowThresh.append(alarmOriginalAreaVO.getConfidenceLevel()).append(Constants.COMMA);
                // 其中一个值大于置信度，直接跳出
                if (Float.parseFloat(alarmOriginalAreaVO.getConfidenceLevel()) >= suppressThresh) {
                    log.info("告警编码:{},最大置信度:{}.策略中置信度阈值:{}",receiveAlarmVO.getAlarmCode(),alarmOriginalAreaVO.getConfidenceLevel(),suppressThresh);
                    flag = false;
                    break;
                }
            }
            if(flag){
                receiveAlarmVO.setThresh(suppressThresh);
                receiveAlarmVO.setIntercept(AlgorithmEventVO.FOR_THRESH);
                receiveAlarmVO.setNowThresh(nowThresh.substring(Constants.ZERO_NUM, nowThresh.length() - Constants.ONE_NUM));
                log.info("告警编码:{},置信度压制",receiveAlarmVO.getAlarmCode());
                return true;
            }
        }
        return false;
    }
}
