package com.chinatower.order.handler.strategy.impl;

import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.order.handler.deal.*;
import com.chinatower.order.handler.strategy.IOrderRelationOutOfTransactionStrategy;
import com.chinatower.platform.constants.OrderConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName OrderRelationUpdateStrategy
 * @Deacription 变更订购关系
 * @Author chenmf
 * @Date 2022/1/7 9:54
 * @Version 1.0
 **/
@Component
@Slf4j
public class OrderRelationUpdateOutOfTransactionStrategy implements IOrderRelationOutOfTransactionStrategy {

    @Autowired
    private ChangeOrderDeviceSyncDeal changeOrderDeviceSyncDeal;
    @Autowired
    private ChangeOrderDeviceCascadeDeal changeOrderDeviceCascadeDeal;
    @Autowired
    private AddOrderDeviceSyncDeal addOrderDeviceSyncDeal;

    @Override
    public boolean judgeProcess(int status) {
        return status == OrderConstants.STATUS_FOR_UPDATE;
    }

    @Override
    public void orderRelationship(OrderGatewayDTO orderGateway) {
        log.info("事务外订购关系处理开始");
        changeOrderDeviceSyncDeal.setNext(addOrderDeviceSyncDeal);
        addOrderDeviceSyncDeal.setNext(changeOrderDeviceCascadeDeal);
        changeOrderDeviceSyncDeal.orderRelationshipDeal(orderGateway);
        log.info("事务外订购关系处理完成");
    }
}
