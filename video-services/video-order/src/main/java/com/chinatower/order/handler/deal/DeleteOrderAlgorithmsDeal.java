package com.chinatower.order.handler.deal;

import com.alibaba.nacos.common.utils.MapUtils;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.order.repository.IOrderAlgorithmsRepository;
import com.chinatower.order.vo.AlgorithmByOrderVO;
import com.chinatower.order.vo.AlgorithmOneVO;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.OrderConstants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName DeleteOrderAlgorithmsDeal
 * @Deacription 订单退订算法关系通用模块
 * @Author chenmf
 * @Date 2022/1/7 10:58
 * @Version 1.0
 **/
@Component
@Slf4j
public class DeleteOrderAlgorithmsDeal extends AbstractOrderDeal {

    @Autowired
    private IOrderAlgorithmsRepository orderAlgorithmsRepository;

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {

        String orderCode = orderGateway.getOrderCode();
        int orderStatus = orderGateway.getStatus();
        String customerCode = orderGateway.getCustomer().getCustomerCode();

        List<OrderGatewayDTO.OrderPackages> packageList = orderGateway.getPackages();
        if (CollectionUtils.isNotEmpty(packageList)) {

            AlgorithmByOrderVO algorithmByOrderVO = new AlgorithmByOrderVO();
            algorithmByOrderVO.setCustomerCode(customerCode);
            algorithmByOrderVO.setOrderCode(orderCode);
            algorithmByOrderVO.setOrdStatus(orderStatus);
            algorithmByOrderVO.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL);
            //算法停用并通知算法仓
            List<AlgorithmOneVO> areaList = orderAlgorithmsRepository.getAlgorithmAreaByOrder(algorithmByOrderVO);
            // 匹配到需要更改的配置id列表
            List<String> configIdList = areaList.stream().map(AlgorithmOneVO::getConfigId).distinct().collect(Collectors.toList());
            orderAlgorithmsRepository.updateAlgorithmStatusByConfigId(DeviceConstants.ALGORITHM_STATUS_STOP, configIdList);
        }

        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }
    }
}
