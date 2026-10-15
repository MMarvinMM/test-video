package com.chinatower.receive.core.intercept;

import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.vo.AlgorithmEventVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.receive.core.util.StrategyTypeEnum;
import com.chinatower.receive.vo.entity.VmAlarmSuppressStrategyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


/**
 * @className: ConfidenceStrategyBySatellite
 * @description: 卫星告警压制方式-置信度压制策略实现类
 * @author: lyy
 * @date: 2024/08/01 15:35
 */
@Component("wxConfidenceStrategy")
@Slf4j
public class ConfidenceStrategyBySatellite extends BaseAlarmSuppressStategy {

    @Override
    public boolean apply(ReceiveAlarmVO receiveAlarmVO, VmAlarmSuppressStrategyVO strategy) {
        Boolean suppressFlag = false;
        if(strategy.getSuppressThresh() != null){
            suppressFlag = doInterceptByThreshBySatellite(strategy,receiveAlarmVO);
        }
        return suppressFlag;
    }

    /**
     * @description: 通过置信度阈值判断是否压制 true为压制，false为不压制
     * @param strategy,receiveAlarmVO
     * @return: boolean
     */
    private Boolean doInterceptByThreshBySatellite(VmAlarmSuppressStrategyVO strategy, ReceiveAlarmVO receiveAlarmVO) {
        float suppressThresh = strategy.getSuppressThresh();
        StringBuilder nowThresh = new StringBuilder();
        if (!StringUtils.isEmpty(receiveAlarmVO.getConfidenceLevel())) {
            //存在综合置信度，用综合置信度压制
            log.info("告警编码:{},综合置信度:{}.策略中置信度阈值:{}",receiveAlarmVO.getAlarmCode(),receiveAlarmVO.getConfidenceLevel(),suppressThresh);
            nowThresh.append(receiveAlarmVO.getConfidenceLevel());
            if (Float.parseFloat(receiveAlarmVO.getConfidenceLevel()) < suppressThresh) {
                receiveAlarmVO.setThresh(suppressThresh);
                receiveAlarmVO.setIntercept(AlgorithmEventVO.FOR_THRESH);
                receiveAlarmVO.setNowThresh(nowThresh.toString());
                log.info("告警编码:{},综合置信度压制",receiveAlarmVO.getAlarmCode());
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return Constants.TWO_NUM;
    }

    @Override
    public boolean getStrategyBeanName(String stategyType) {
        return StrategyTypeEnum.WX_CONFIDENCE_STRATEGY.getDisplayName().equals(stategyType);
    }
}
