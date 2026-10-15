package com.chinatower.order.handler.strategy.impl;

import com.chinatower.order.handler.deal.*;
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
public class OrderRelationDeactivateStrategy extends DefaultOrderRelationStrategy {
    
    @Autowired
    private EnableAlgorithmsDeal deviceAlgorithmsDeal;
    @Autowired
    private ChangeOrderDeptStreetRel changeOrderDeptStreetRel;
    
    @Override
    public boolean judgeProcess(int status) {
        return status == OrderConstants.STATUS_FOR_DEACTIVATE;
    }
    
    
    @Override
    public AbstractOrderDeal orderRelationship() {
        log.info("订购停用关系处理");
        deviceAlgorithmsDeal.setNext(changeOrderDeptStreetRel);
        return deviceAlgorithmsDeal;
    }
}
