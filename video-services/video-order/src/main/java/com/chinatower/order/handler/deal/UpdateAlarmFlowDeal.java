package com.chinatower.order.handler.deal;

import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.core.dto.OrderGatewayDTO.OrderPackages;
import com.chinatower.order.service.IOrderSystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
*保存角色信息 初始化告警流程配置
*@author liu.chao
*@date 2022/8/10
*/
@Slf4j
@Component
public class UpdateAlarmFlowDeal extends AbstractOrderDeal {
    @Autowired
    private IOrderSystemService orderSystemService;

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {
        log.info("=======保存角色信息 初始化告警流程配置=========={}",orderGateway.getOrderCode());
        OrderGatewayDTO.Customer customer = orderGateway.getCustomer();
        String userId = orderSystemService.findUserIdByUserName(customer.getAdmin());
        List<OrderPackages> packages = orderGateway.getPackages();
        Map<String, String> industryMap = packages.stream()
                .collect(Collectors.toMap(x -> x.getApplication().getAppCode(), x -> x.getApplication().getProductCode(), (a, b) -> a));
        orderSystemService.insertUserRoleBatch(userId, industryMap);

        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }
    }
}
