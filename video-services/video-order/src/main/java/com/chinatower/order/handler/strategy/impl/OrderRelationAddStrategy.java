package com.chinatower.order.handler.strategy.impl;

import com.chinatower.order.handler.deal.AbstractOrderDeal;
import com.chinatower.order.handler.deal.HandleAlarmSceneDeal;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.order.handler.deal.AddDeviceRootOrganDeal;
import com.chinatower.order.handler.deal.SaveOrUpdateDeviceDeal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName OrderRelationAddStrategy
 * @Deacription 新增订购关系
 * @Author chenmf
 * @Date 2022/1/7 9:54
 * @Version 1.0
 **/
@Component
@Slf4j
public class OrderRelationAddStrategy extends DefaultOrderRelationStrategy {
    
    @Autowired
    private AddDeviceRootOrganDeal deviceRootOrganDeal;
    @Autowired
    private SaveOrUpdateDeviceDeal saveOrUpdateDeviceDeal;
    @Autowired
    private HandleAlarmSceneDeal handleAlarmSceneDeal;
    
    @Override
    public boolean judgeProcess(int status) {
        return status == OrderConstants.STATUS_FOR_ADD;
    }
    
    @Override
    public AbstractOrderDeal orderRelationship() {
        log.info("订购新增关系处理");
        saveOrUpdateDeviceDeal.setNext(deviceRootOrganDeal);
        deviceRootOrganDeal.setNext(handleAlarmSceneDeal);
        return saveOrUpdateDeviceDeal;
    }
}
