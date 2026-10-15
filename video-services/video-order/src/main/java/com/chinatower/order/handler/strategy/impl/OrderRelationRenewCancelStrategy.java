package com.chinatower.order.handler.strategy.impl;

import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.order.handler.deal.ChangeFrontAlarmConfigDeal;
import com.chinatower.order.handler.deal.ChangeOrderDeptStreetRel;
import com.chinatower.order.handler.deal.DeleteOrderAlgorithmsDeal;
import com.chinatower.order.handler.deal.DeleteOrderDeviceSetDeal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 续订退订策略类
 */
@Component
@Slf4j
public class OrderRelationRenewCancelStrategy{

    @Autowired
    private DeleteOrderDeviceSetDeal orderDeviceSetDeal;
    @Autowired
    private DeleteOrderAlgorithmsDeal deleteOrderAlgorithmsDeal;
    @Autowired
    private ChangeFrontAlarmConfigDeal changeFrontAlarmConfigDeal;
    @Autowired
    private ChangeOrderDeptStreetRel changeOrderDeptStreetRel;

    public void orderRelationship(OrderGatewayDTO orderGateway) {
        log.info("订购续订退订关系处理开始");
        orderDeviceSetDeal.setNext(deleteOrderAlgorithmsDeal);
        deleteOrderAlgorithmsDeal.setNext(changeFrontAlarmConfigDeal);
        changeFrontAlarmConfigDeal.setNext(changeOrderDeptStreetRel);

        orderDeviceSetDeal.orderRelationshipDeal(orderGateway);
        log.info("订购续订退订关系处理完成");
    }
}