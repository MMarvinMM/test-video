package com.chinatower.order.handler.deal;

import com.chinatower.common.core.domain.*;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.order.repository.IOrderUavDeviceHisRepository;
import com.chinatower.order.repository.IOrderUavDeviceRepository;
import com.chinatower.platform.constants.OrderConstants;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class OrderUavDeviceDeal extends AbstractOrderDeal {

    @Autowired
    private IOrderUavDeviceHisRepository orderUavDeviceHisRepository;

    @Autowired
    private IOrderUavDeviceRepository orderUavDeviceRepository;

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {
        log.info("OrderUavDeviceDeal 订购处理开始");
        String orderCode = orderGateway.getOrderCode();
        List<String> orderCodeList = Lists.newArrayList();
        orderCodeList.add(orderCode);
        List<OrderUavDeviceDO> orderDeviceList = orderUavDeviceRepository.getOrderDeviceByList(orderCodeList, null);
        int moveSign = orderGateway.getOrderMoveSign();
        if (!CollectionUtils.isEmpty(orderDeviceList)) {
            List<OrderUavDeviceHisDO> orderDeviceHisList = new ArrayList<>();
            for (OrderUavDeviceDO orderDeviceDO : orderDeviceList) {
                OrderUavDeviceHisDO hisDO = new OrderUavDeviceHisDO();
                BeanUtils.copyProperties(orderDeviceDO, hisDO);
                orderDeviceHisList.add(hisDO);
            }
            orderUavDeviceHisRepository.saveListBatch(orderDeviceHisList);
            orderUavDeviceRepository.deleteByOrderCode(orderCode);
            this.saveOrderUavDevice(orderGateway, moveSign);
        } else {
            this.saveOrderUavDevice(orderGateway, moveSign);
        }
        log.info("OrderUavDeviceDeal 订购处理结束");
        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }

    }

    private void saveOrderUavDevice(OrderGatewayDTO orderGateway, int moveSign) {

        String orderCode = orderGateway.getOrderCode();
        String customerCode = orderGateway.getCustomer().getCustomerCode();
        List<OrderGatewayDTO.OrderPackages> packageList = orderGateway.getPackages();

        if (!CollectionUtils.isEmpty(packageList)) {
            String oldAcceptNode = orderGateway.getOldAcceptNode();
            List<OrderUavDeviceDO> orderSensorDeviceDOList = new ArrayList<>();
            for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
                String appCode = orderPackages.getApplication().getAppCode();
                String productCode = orderPackages.getApplication().getProductCode();
                List<OrderGatewayDTO.Device> uavDeviceList = orderPackages.getUavDevice();
                if (!CollectionUtils.isEmpty(uavDeviceList)) {
                    for (OrderGatewayDTO.Device uavDevice : uavDeviceList) {
                        //传感器
                        OrderUavDeviceDO orderUavDeviceDO = new OrderUavDeviceDO();
                        orderUavDeviceDO.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL);
                        orderUavDeviceDO.setAreaCode(uavDevice.getAreaCode());
                        orderUavDeviceDO.setAdmCode(uavDevice.getAdmCode());
                        if (!Objects.isNull(uavDevice.getStatus())) {
                            orderUavDeviceDO.setStatus(uavDevice.getStatus());
                        }
                        orderUavDeviceDO.setLatitude(uavDevice.getLatitude());
                        orderUavDeviceDO.setLongitude(uavDevice.getLongitude());
                        orderUavDeviceDO.setLocation(uavDevice.getLocation());
                        orderUavDeviceDO.setTenantId(customerCode);
                        orderUavDeviceDO.setDeviceCode(uavDevice.getDeviceCode());
                        orderUavDeviceDO.setDeviceName(uavDevice.getDevName());
                        orderUavDeviceDO.setPackageCode(orderPackages.getPackageCode());
                        orderUavDeviceDO.setOrderCode(orderCode);
                        orderUavDeviceDO.setMoveSign(moveSign);
                        orderUavDeviceDO.setCreateTime(DateUtils.getNowDate());
                        orderUavDeviceDO.setUpdateTime(DateUtils.getNowDate());
                        orderUavDeviceDO.setOrdStatus(orderGateway.getStatus());
                        orderUavDeviceDO.setIndustryCode(appCode);
                        orderUavDeviceDO.setAppVerCode(productCode);
                        // 如果订单受理节点非当前节点，不更改订单受理节点信息
                        if (BooleanUtils.isTrue(orderGateway.getHandleOrderUserInfoOnly()) && StringUtils.isNotEmpty(oldAcceptNode)) {
                            orderUavDeviceDO.setAcceptNode(oldAcceptNode);
                        } else {
                            orderUavDeviceDO.setAcceptNode(orderGateway.getAcceptNode());
                        }
                        orderUavDeviceDO.setAccessNode(uavDevice.getAccessNode());
                        orderSensorDeviceDOList.add(orderUavDeviceDO);
                    }
                }
            }
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(orderSensorDeviceDOList)) {
                orderUavDeviceRepository.saveListBatch(orderSensorDeviceDOList);
            }
        }
    }
}
