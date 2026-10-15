package com.chinatower.order.handler.strategy.impl;

import com.chinatower.order.handler.deal.AbstractOrderDeal;
import com.chinatower.order.handler.deal.ChangeOrderDeptStreetRel;
import com.chinatower.order.handler.deal.EnableAlgorithmsDeal;
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
public class OrderRelationEnableStrategy extends DefaultOrderRelationStrategy {

    @Autowired
    private EnableAlgorithmsDeal deviceAlgorithmsDeal;
    @Autowired
    private ChangeOrderDeptStreetRel changeOrderDeptStreetRel;

    @Override
    public boolean judgeProcess(int status) {
        return status == OrderConstants.STATUS_FOR_ENABLE;
    }


    @Override
    public AbstractOrderDeal orderRelationship() {
        log.info("订购启用关系处理");
        deviceAlgorithmsDeal.setNext(changeOrderDeptStreetRel);
        return deviceAlgorithmsDeal;
    }
}
