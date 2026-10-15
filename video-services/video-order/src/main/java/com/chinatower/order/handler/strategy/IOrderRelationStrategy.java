package com.chinatower.order.handler.strategy;

import com.chinatower.common.core.dto.OrderGatewayDTO;

/**
 * 功能描述  订单推送关系策略接口
 * @author chenmf
 * @date 2022/1/7
 *
 * @return
 */
public interface IOrderRelationStrategy {
    

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
