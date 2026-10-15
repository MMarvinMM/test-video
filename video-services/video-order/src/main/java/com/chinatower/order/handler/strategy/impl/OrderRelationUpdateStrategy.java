package com.chinatower.order.handler.strategy.impl;

import com.chinatower.order.handler.deal.*;
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
public class OrderRelationUpdateStrategy extends DefaultOrderRelationStrategy {

    @Autowired
    private ChangeOrderDeviceSetDeal changeOrderDeviceSetDeal;
    @Autowired
    private ChangeAlgorithmsDeal reduceAlgorithmsDeal;
    @Autowired
    private ChangeOrderDeptStreetRel changeOrderDeptStreetRel;
    @Autowired
    private UpdateAlarmFlowDeal updateAlarmFlowDeal;
    @Autowired
    private ChangeOrderSensorDeviceSetDeal changeOrderSensorDeviceSetDeal;
    @Autowired
    private ChangeOrderServiceRelSetDeal changeOrderServiceRelSetDeal;
    @Autowired
    private ChangeOrderUavDeviceSetDeal changeOrderUavDeviceSetDeal;
    @Autowired
    private ChangeOrderUavNestDeviceSetDeal changeOrderUavNestDeviceSetDeal;
    @Autowired
    private ChangeOrderHornDeviceSetDeal changeOrderHornDeviceSetDeal;
    @Autowired
    private ChangeOrderRecorderDeviceSetDeal changeOrderRecorderDeviceSetDeal;
    @Autowired
    private HandleAlarmSceneDeal handleAlarmSceneDeal;

    @Override
    public boolean judgeProcess(int status) {
        return status == OrderConstants.STATUS_FOR_UPDATE;
    }


    @Override
    public AbstractOrderDeal orderRelationship() {
        log.info("订购变更关系处理");
        //0808日更新需求-去掉物联设备的新增和减少，卫星数据的新增和减少
        reduceAlgorithmsDeal.setNext(changeOrderDeviceSetDeal);
        changeOrderDeviceSetDeal.setNext(changeOrderSensorDeviceSetDeal);
        changeOrderSensorDeviceSetDeal.setNext(changeOrderServiceRelSetDeal);
        changeOrderServiceRelSetDeal.setNext(changeOrderUavDeviceSetDeal);
        changeOrderUavDeviceSetDeal.setNext(changeOrderUavNestDeviceSetDeal);
        changeOrderUavNestDeviceSetDeal.setNext(changeOrderHornDeviceSetDeal);
        changeOrderHornDeviceSetDeal.setNext(changeOrderRecorderDeviceSetDeal);
        changeOrderRecorderDeviceSetDeal.setNext(updateAlarmFlowDeal);
        updateAlarmFlowDeal.setNext(changeOrderDeptStreetRel);
        changeOrderDeptStreetRel.setNext(handleAlarmSceneDeal);
        return reduceAlgorithmsDeal;
    }
}
