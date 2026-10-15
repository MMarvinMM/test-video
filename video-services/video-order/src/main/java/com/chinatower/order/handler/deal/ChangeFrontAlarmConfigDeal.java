package com.chinatower.order.handler.deal;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.domain.OrderDeviceHisDO;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.IAlgorithmConfigService;
import com.chinatower.order.repository.IOrderDeviceHisRepository;
import com.chinatower.platform.entity.device.AlgorithmGiveMergeOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理前端告警配置链
 */
@Component
@Slf4j
public class ChangeFrontAlarmConfigDeal extends AbstractOrderDeal {

    @Autowired
    private IAlgorithmConfigService iAlgorithmConfigService;

    @Autowired
    private IOrderDeviceHisRepository orderDeviceHisRepository;

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {
        // 前端告警
        String orderCode = orderGateway.getOrderCode();
        String tenantId = orderGateway.getCustomer().getCustomerCode();


        // 如果oldOrderCode为空，则不是续订，调用停用无人机计划、算法；续订不调用无人机计划、算法接口
        if (StringUtils.isEmpty(orderGateway.getOldOrderCode())) {
            // 运管退订报文传参的设备不全，获取上一次订购的报文当做全量的设备信息
            int moveSign = orderGateway.getOrderMoveSign() - 1;
            List<OrderDeviceHisDO> reduceDeviceList = orderDeviceHisRepository.getOldOrderDeviceByOrderCode(orderCode, moveSign);
            // 调用接口停用无人机计划、算法
            if (CollectionUtils.isNotEmpty(reduceDeviceList)) {
                List<String> reduceDeviceCodeList = reduceDeviceList.stream().map(OrderDeviceHisDO::getDeviceCode).distinct().collect(Collectors.toList());
                for (OrderGatewayDTO.OrderPackages orderPackage : orderGateway.getPackages()) {
//            if (CollectionUtils.isEmpty(orderPackage.getDevices())) {
//                continue;
//            }
                    String industryCode = orderPackage.getApplication().getAppCode();

                    try {
                        AlgorithmGiveMergeOrderVO request = new AlgorithmGiveMergeOrderVO();
                        request.setIndustryCode(industryCode);
                        request.setOrderCode(orderCode);
                        request.setTenantId(tenantId);
                        request.setDeviceCodeList(reduceDeviceCodeList);
                        Result<String> result = iAlgorithmConfigService.algorithmGiveMergeOrder(request);
                        log.info("ChangeFrontAlarmConfigDeal algorithmGiveMergeOrder param:{}, result:{}",
                                JSON.toJSONString(request), JSON.toJSONString(result));
                    } catch (Exception e) {
                        log.error("ChangeFrontAlarmConfigDeal 处理前端告警配置失败 orderCode:{}, tenantId:{}, e:", orderCode, tenantId, e);
                    }
                }
            }
        }



        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }
    }
}
