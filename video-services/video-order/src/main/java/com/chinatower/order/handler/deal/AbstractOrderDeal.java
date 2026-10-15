package com.chinatower.order.handler.deal;

import com.chinatower.common.core.dto.OrderGatewayDTO;

/**
 * @ClassName AbstractOrderDeal
 * @Deacription 抽象订购关系
 * @Author chenmf
 * @Date 2022/1/7 10:56
 * @Version 1.0
 **/
public abstract class AbstractOrderDeal {
    
    private AbstractOrderDeal next;
    
    public void setNext(AbstractOrderDeal next) {
        this.next = next;
    }
    public AbstractOrderDeal getNext() {
        return next;
    }
    
    /**
     * 功能描述 处理逻辑
     * @author chenmf
     * @date 2022/1/7
     * [orderGateway]
     * @return
     */
    public abstract void orderRelationshipDeal(OrderGatewayDTO orderGateway);
    

}
