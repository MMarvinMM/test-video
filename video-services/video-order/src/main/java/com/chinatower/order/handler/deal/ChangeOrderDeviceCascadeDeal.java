package com.chinatower.order.handler.deal;

import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.device.api.ICasCadeSyncService;
import com.chinatower.platform.entity.device.CasCadeSyncVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ChangeOrderDeviceCascadeDeal extends AbstractOrderDeal {

    @Autowired
    private ICasCadeSyncService iCasCadeSyncService;

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {
        String tenantId = orderGateway.getCustomer().getCustomerCode();
        // 调用级联接口处理级联同步逻辑
        try {
            CasCadeSyncVO param = new CasCadeSyncVO();
            param.setTenantId(tenantId);
            iCasCadeSyncService.synDelCascadeByOrder(param);
        } catch (Exception e) {
            log.error("ChangeOrderDeviceCascadeDeal synDelCascadeByOrder error, e:", e);
        }
    }
}
