package com.chinatower.receive.core.intercept;

import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.receive.vo.entity.VmAlarmSuppressStrategyVO;

/**
 * @className: SuppressionStrategy
 * @description: 告警压制策略接口（废弃）
 * @author: lyy
 * @date: 2024/08/01 15:30
 * @requestID 告警接收逻辑迁移
 */
public interface SuppressionStrategy {

    /**
     * 实现策略逻辑
     * @param receiveAlarmVO
     * @param strategy
     * @return
     */
    boolean apply(ReceiveAlarmVO receiveAlarmVO, VmAlarmSuppressStrategyVO strategy);

    /**
     * 返回策略方式顺序
     * @return
     */
    int getOrder();

    /**
     * 获取压制策略的Bean
     * @param stategyType
     * @return
     */
    boolean getStrategyBeanName(String stategyType);
}
