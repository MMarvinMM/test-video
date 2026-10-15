package com.chinatower.order.handler.strategy.impl;

import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.order.handler.deal.*;
import com.chinatower.order.handler.strategy.IOrderRelationOutOfTransactionStrategy;
import com.chinatower.platform.constants.OrderConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName OrderRelationDeactivateStrategy
 * @Deacription 停用
 * @Author chenmf
 * @Date 2022/1/7 9:54
 * @Version 1.0
 **/
@Component
@Slf4j
public class OrderRelationDeactivateOutOfTransactionStrategy implements IOrderRelationOutOfTransactionStrategy {
    
    @Autowired
    private EnableOrderDeviceSetDeal enableOrderDeviceSetDeal;
    @Autowired
    private ChangeFrontAlarmConfigDeal changeFrontAlarmConfigDeal;
    @Autowired
    private ChangeOrderDeviceCascadeDeal changeOrderDeviceCascadeDeal;

    @Override
    public boolean judgeProcess(int status) {
        return status == OrderConstants.STATUS_FOR_DEACTIVATE;
    }
    

    @Override
    public void orderRelationship(OrderGatewayDTO orderGateway) {

        log.info("事务外订购关系处理开始");

        enableOrderDeviceSetDeal.setNext(changeFrontAlarmConfigDeal);
        changeFrontAlarmConfigDeal.setNext(changeOrderDeviceCascadeDeal);
        enableOrderDeviceSetDeal.orderRelationshipDeal(orderGateway);
        log.info("事务外订购关系处理完成");
    }
}
