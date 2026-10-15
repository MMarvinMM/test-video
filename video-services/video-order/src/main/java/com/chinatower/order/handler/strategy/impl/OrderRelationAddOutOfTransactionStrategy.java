package com.chinatower.order.handler.strategy.impl;

import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.order.handler.deal.AddOrderDeviceSyncDeal;
import com.chinatower.order.handler.strategy.IOrderRelationOutOfTransactionStrategy;
import com.chinatower.platform.constants.OrderConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderRelationAddOutOfTransactionStrategy implements IOrderRelationOutOfTransactionStrategy {

    @Autowired
    private AddOrderDeviceSyncDeal addOrderDeviceSyncDeal;

    @Override
    public boolean judgeProcess(int status) {
        return status == OrderConstants.STATUS_FOR_ADD;
    }

    @Override
    public void orderRelationship(OrderGatewayDTO orderGateway) {
        log.info("事务外订购关系处理开始");
        addOrderDeviceSyncDeal.orderRelationshipDeal(orderGateway);
        log.info("事务外订购关系处理完成");
    }
}
