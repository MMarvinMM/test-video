package com.chinatower.order.handler.strategy.impl;

import com.chinatower.order.handler.deal.AbstractOrderDeal;
import com.chinatower.order.handler.deal.OrderLocationSyncDeal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderRelationLocationStrategy extends DefaultOrderRelationStrategy {
    
    @Autowired
    private OrderLocationSyncDeal orderLocationSyncDeal;

    @Override
    public boolean judgeProcess(int status) {
        return false;
    }
    
    @Override
    public AbstractOrderDeal orderRelationship() {
        log.info("订购属地化订购关系处理");
        return orderLocationSyncDeal;
    }
}
