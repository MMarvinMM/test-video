package com.chinatower.order.handler.strategy.impl;

import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.order.handler.deal.*;
import com.chinatower.order.handler.strategy.IOrderRelationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName OrderRelationAddStrategy
 * @Deacription 默认订购关系处理逻辑
 * @Author chenmf
 * @Date 2022/1/7 9:54
 * @Version 1.0
 **/
@Component
@Slf4j
public abstract class DefaultOrderRelationStrategy implements IOrderRelationStrategy {

    @Autowired
    private OrderAlgorithmsDeal orderAlgorithmsDeal;
    @Autowired
    private OrderCustomerDeal orderCustomerDeal;
    @Autowired
    private OrderDeviceDeal orderDeviceDeal;
    @Autowired
    private OrderPackageDeal orderPackageDeal;
    @Autowired
    private OrderSensorDeviceDeal orderSensorDeviceDeal;
    @Autowired
    private OrderServiceRelDeal orderServiceRelDeal;
    @Autowired
    private OrderUavDeviceDeal orderUavDeviceDeal;
    @Autowired
    private OrderUavNestDeviceDeal orderUavNestDeviceDeal;
    @Autowired
    private OrderHornDeviceDeal orderHornDeviceDeal;
    @Autowired
    private OrderRecorderDeviceDeal orderRecorderDeviceDeal;
    @Autowired
    private OrderEnergyDeviceDeal orderEnergyDeviceDeal;

    @Override
    public void orderRelationship(OrderGatewayDTO orderGateway) {

        log.info("订购关系处理开始");

        orderCustomerDeal.setNext(orderPackageDeal);
        orderPackageDeal.setNext(orderDeviceDeal);
        orderDeviceDeal.setNext(orderSensorDeviceDeal);
        orderSensorDeviceDeal.setNext(orderAlgorithmsDeal);
        orderAlgorithmsDeal.setNext(orderServiceRelDeal);
        orderServiceRelDeal.setNext(orderUavDeviceDeal);
        orderUavDeviceDeal.setNext(orderUavNestDeviceDeal);
        orderUavNestDeviceDeal.setNext(orderHornDeviceDeal);
        orderHornDeviceDeal.setNext(orderRecorderDeviceDeal);
        orderRecorderDeviceDeal.setNext(orderEnergyDeviceDeal);
        orderEnergyDeviceDeal.setNext(orderRelationship());

        orderCustomerDeal.orderRelationshipDeal(orderGateway);
        log.info("订购关系处理完成");
    }

    /**
     * 特殊处理
     *
     * @return
     * @author chenmf
     * @date 2022/5/30
     * @version
     */
    public abstract AbstractOrderDeal orderRelationship();

}
