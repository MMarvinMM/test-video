package com.chinatower.order.handler.strategy.impl;

import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.order.handler.deal.EnableOrderDeviceSetDeal;
import com.chinatower.order.handler.strategy.IOrderRelationOutOfTransactionStrategy;
import com.chinatower.platform.constants.OrderConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName OrderRelationEnableStrategy
 * @Deacription 订单续订、启用关系
 * @Author chenmf
 * @Date 2022/1/7 9:54
 * @Version 1.0
 **/
@Component
@Slf4j
public class OrderRelationEnableOutOfTransactionStrategy implements IOrderRelationOutOfTransactionStrategy {

    @Autowired
    private EnableOrderDeviceSetDeal enableOrderDeviceSetDeal;

    @Override
    public boolean judgeProcess(int status) {
        return status == OrderConstants.STATUS_FOR_ENABLE;
    }

    @Override
    public void orderRelationship(OrderGatewayDTO orderGateway) {
        log.info("事务外订购关系处理开始");
        enableOrderDeviceSetDeal.orderRelationshipDeal(orderGateway);
        log.info("事务外订购关系处理完成");
    }
}
