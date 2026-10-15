package com.chinatower.order.handler.deal;

import com.chinatower.common.core.domain.OrderPackageDO;
import com.chinatower.common.core.domain.OrderPackageHisDO;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.core.dto.OrderGatewayDTO.OrderPackages;
import com.chinatower.common.core.dto.OrderGatewayDTO.Application;
import com.chinatower.common.core.utils.DateUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.order.repository.IOrderPackageHisRepository;
import com.chinatower.order.repository.IOrderPackageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName OrderPackageDeal
 * @Deacription 订单套餐应用关系通用模块
 * @Author chenmf
 * @Date 2022/1/7 10:58
 * @Version 1.0
 **/
@Component
@Slf4j
public class OrderPackageDeal extends AbstractOrderDeal{

    @Autowired
    private IOrderPackageHisRepository orderPackageHisRepository;

    @Autowired
    private IOrderPackageRepository orderPackageRepository;

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {

        String orderCode = orderGateway.getOrderCode();
        List<String> orderCodeList = Lists.newArrayList();
        orderCodeList.add(orderCode);
        List<OrderPackageDO> orderPackageList = orderPackageRepository.getOrderPackageByOrderCodeList(orderCodeList, null);
        int moveSign = orderGateway.getOrderMoveSign();
        if (!CollectionUtils.isEmpty(orderPackageList)) {
            List<OrderPackageHisDO> orderPackageHisDOList = new ArrayList<>();
            for (OrderPackageDO orderPackageDO : orderPackageList) {
                OrderPackageHisDO hisDO = new OrderPackageHisDO();
                BeanUtils.copyProperties(orderPackageDO, hisDO);
                orderPackageHisDOList.add(hisDO);
            }
            orderPackageHisRepository.saveListBatch(orderPackageHisDOList);
            orderPackageRepository.deleteByOrderCode(orderCode);
            this.saveNewOrderPackageList(orderGateway, moveSign);
        }
        else {
            this.saveNewOrderPackageList(orderGateway, moveSign);
        }

        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }
    }

    /**
     * 功能描述 保存新的订单套餐应用关系数据
     * @author chenmf
     * @date 2022/1/10
     * [orderGateway, moveSign]
     * @return
     */
    private void saveNewOrderPackageList(OrderGatewayDTO orderGateway, int moveSign) {

        List<OrderPackages> packageList = orderGateway.getPackages();
        String orderCode = orderGateway.getOrderCode();
        String customerCode = orderGateway.getCustomer().getCustomerCode();

        if (!CollectionUtils.isEmpty(packageList)) {
            String oldAcceptNode = orderGateway.getOldAcceptNode();
            List<OrderPackageDO> orderPackageDOList = new ArrayList<>();
            for (OrderPackages orderPackages : packageList) {
                OrderPackageDO orderPackageDO = new OrderPackageDO();
                Application application = orderPackages.getApplication();
                orderPackageDO.setOrderCode(orderCode);
                orderPackageDO.setIndustryCode(application.getAppCode());
                orderPackageDO.setDescription(orderPackages.getDescription());
                StringBuilder capabilitiesStr = new StringBuilder();
                List<String> capabilities = application.getCapabilities();
                for (int j = 0; j < capabilities.size(); j++) {
                    capabilitiesStr.append(capabilities.get(j)).append(",");
                }
                orderPackageDO.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL);
                orderPackageDO.setCapabilitiesStr(capabilitiesStr.toString());
                orderPackageDO.setAppVerCode(application.getProductCode());
                orderPackageDO.setAppVerName(application.getProductName());
                orderPackageDO.setTenantId(customerCode);
                orderPackageDO.setPackageCode(orderPackages.getPackageCode());
                orderPackageDO.setPackageName(orderPackages.getPackageName());
                orderPackageDO.setStorageTime(orderPackages.getStorageTime());
                orderPackageDO.setCreateTime(DateUtils.getNowDate());
                orderPackageDO.setUpdateTime(DateUtils.getNowDate());
                orderPackageDO.setOrdStatus(orderGateway.getStatus());
                orderPackageDO.setMoveSign(moveSign);
                // 如果订单受理节点非当前节点，不更改订单受理节点信息
                if (BooleanUtils.isTrue(orderGateway.getHandleOrderUserInfoOnly()) && StringUtils.isNotEmpty(oldAcceptNode)) {
                    orderPackageDO.setAcceptNode(oldAcceptNode);
                } else {
                    orderPackageDO.setAcceptNode(orderGateway.getAcceptNode());
                }
                orderPackageDOList.add(orderPackageDO);
            }
            orderPackageRepository.saveListBatch(orderPackageDOList);
        }

    }

}
