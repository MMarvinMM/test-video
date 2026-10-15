package com.chinatower.order.handler.strategy;

import com.chinatower.common.core.dto.OrderGatewayDTO;

/**
 * 功能描述  订单推送关系事务外策略接口
 */
public interface IOrderRelationOutOfTransactionStrategy {
    

    /**
     * 功能描述  订单推送关系判断
     * @author chenmf
     * @date 2022/1/7
     * []
     * @return
     */
    boolean judgeProcess(int status);
    
    
    void orderRelationship(OrderGatewayDTO orderGateway);
}
