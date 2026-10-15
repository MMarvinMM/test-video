package com.chinatower.order.handler.deal;

import com.chinatower.common.core.dto.OrderGatewayDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName ChangeStorageTimeDeal
 * @Deacription 存储时长变更处理模块（暂无功能）
 * @Author chenmf
 * @Date 2022/2/23 9:20
 * @Version 1.0
 **/
@Component
@Slf4j
public class ChangeStorageTimeDeal extends AbstractOrderDeal{
    
    
    
    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {
    
    
        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }
    }
}
