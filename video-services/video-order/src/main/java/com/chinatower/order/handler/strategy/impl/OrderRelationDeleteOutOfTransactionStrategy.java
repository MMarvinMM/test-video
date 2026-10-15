package com.chinatower.order.handler.strategy.impl;

import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.order.handler.deal.*;
import com.chinatower.order.handler.strategy.IOrderRelationOutOfTransactionStrategy;
import com.chinatower.platform.constants.OrderConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName OrderRelationDeleteStrategy
 * @Deacription 退订
 * @Author chenmf
 * @Date 2022/1/7 9:54
 * @Version 1.0
 **/
@Component
@Slf4j
public class OrderRelationDeleteOutOfTransactionStrategy implements IOrderRelationOutOfTransactionStrategy {

    @Autowired
    private DeleteOrderDeviceSetDeal orderDeviceSetDeal;
    @Autowired
    private ChangeOrderDeviceCascadeDeal changeOrderDeviceCascadeDeal;
    @Autowired
    private ChangeFrontAlarmConfigDeal changeFrontAlarmConfigDeal;

    @Override
    public boolean judgeProcess(int status) {
        return status == OrderConstants.STATUS_FOR_DELETE;
    }
    
    @Override
    public void orderRelationship(OrderGatewayDTO orderGateway) {
        log.info("事务外订购关系处理开始");
        orderDeviceSetDeal.setNext(changeFrontAlarmConfigDeal);
        changeFrontAlarmConfigDeal.setNext(changeOrderDeviceCascadeDeal);
        orderDeviceSetDeal.orderRelationshipDeal(orderGateway);
        log.info("事务外订购关系处理完成");
    }
}
