package com.chinatower.order.handler.strategy.impl;

import com.chinatower.order.handler.deal.*;
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
public class OrderRelationDeleteStrategy extends DefaultOrderRelationStrategy {

    @Autowired
    private DeleteOrderAlgorithmsDeal deleteOrderAlgorithmsDeal;
    @Autowired
    private ChangeOrderDeptStreetRel changeOrderDeptStreetRel;

    @Override
    public boolean judgeProcess(int status) {
        return status == OrderConstants.STATUS_FOR_DELETE;
    }
    
    @Override
    public AbstractOrderDeal orderRelationship() {
        log.info("订购退订关系处理");
        deleteOrderAlgorithmsDeal.setNext(changeOrderDeptStreetRel);
        return deleteOrderAlgorithmsDeal;
    }
}
