package com.chinatower.order.handler.deal;

import com.chinatower.common.core.domain.OrderUavDeviceHisDO;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.core.dto.OrderGatewayDTO.OrderPackages;
import com.chinatower.order.service.IOrderOrganDeviceRelService;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.platform.entity.device.DelDeviceRelaDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @ClassName ChangeOrderDeptStreetRel
 * @Deacription 变更订单更新组织行政区域处理模块
 * @Author chenmf
 * @Date 2022/1/7 10:58
 * @Version 1.0
 **/
@Component
@Slf4j
public class ChangeOrderDeptStreetRel extends AbstractOrderDeal{

    @Autowired
    private IOrderOrganDeviceRelService orderOrganDeviceRelService;

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {
        log.info("更新组织行政区域处理模块");

        String customerCode = orderGateway.getCustomer().getCustomerCode();
        List<OrderPackages> packageList = orderGateway.getPackages();
        Map<String, String> industryMap = packageList.stream()
                .collect(Collectors.toMap(x -> x.getApplication().getAppCode(), x -> x.getApplication().getProductCode(), (a, b) -> a));

        orderOrganDeviceRelService.updateDeptStreetRelBatch(customerCode, industryMap);

        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }
    }

}
