package com.chinatower.receive.core.intercept;

import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.receive.vo.entity.VmAlarmSuppressStrategyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 告警压制策略核心策略类
 * @Author: lyy
 * @Date: 2024/09/14 14:17
 * @version release_v1.6.90
 */
@Component("suppressionStrategy")
@Slf4j
public abstract class BaseAlarmSuppressStategy{

    /**
     * 实现各压制策略主逻辑
     * @param receiveAlarmVO
     * @param strategy
     * @return
     */
    public abstract boolean apply(ReceiveAlarmVO receiveAlarmVO, VmAlarmSuppressStrategyVO strategy);

    /**
     * 获取压制策略执行顺序
     * @return
     */
    public abstract int getOrder();

    /**
     * 获取压制策略的Bean名称
     * @param stategyType
     * @return
     */
    public abstract boolean getStrategyBeanName(String stategyType);
}
