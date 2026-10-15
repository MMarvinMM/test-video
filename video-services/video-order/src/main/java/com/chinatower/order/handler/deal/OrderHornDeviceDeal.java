package com.chinatower.order.handler.deal;

import com.chinatower.common.core.domain.OrderHornDeviceDO;
import com.chinatower.common.core.domain.OrderHornDeviceHisDO;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.order.repository.IOrderHornDeviceHisRepository;
import com.chinatower.order.repository.IOrderHornDeviceRepository;
import com.chinatower.platform.constants.OrderConstants;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class OrderHornDeviceDeal extends AbstractOrderDeal {

    @Autowired
    private IOrderHornDeviceHisRepository orderHornDeviceHisRepository;

    @Autowired
    private IOrderHornDeviceRepository orderHornDeviceRepository;

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {
        log.info("OrderHornDeviceDeal 订购处理开始");
        String orderCode = orderGateway.getOrderCode();
        List<String> orderCodeList = Lists.newArrayList();
        orderCodeList.add(orderCode);
        List<OrderHornDeviceDO> orderDeviceList = orderHornDeviceRepository.getOrderDeviceByList(orderCodeList, null);
        int moveSign = orderGateway.getOrderMoveSign();
        if (!CollectionUtils.isEmpty(orderDeviceList)) {
            List<OrderHornDeviceHisDO> orderDeviceHisList = new ArrayList<>();
            for (OrderHornDeviceDO orderDeviceDO : orderDeviceList) {
                OrderHornDeviceHisDO hisDO = new OrderHornDeviceHisDO();
                BeanUtils.copyProperties(orderDeviceDO, hisDO);
                orderDeviceHisList.add(hisDO);
            }
            orderHornDeviceHisRepository.saveListBatch(orderDeviceHisList);
            orderHornDeviceRepository.deleteByOrderCode(orderCode);
            this.saveOrderSensorDevice(orderGateway, moveSign);
        } else {
            this.saveOrderSensorDevice(orderGateway, moveSign);
        }
        log.info("OrderHornDeviceDeal 订购处理结束");
        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }

    }

    private void saveOrderSensorDevice(OrderGatewayDTO orderGateway, int moveSign) {

        String orderCode = orderGateway.getOrderCode();
        String customerCode = orderGateway.getCustomer().getCustomerCode();
        List<OrderGatewayDTO.OrderPackages> packageList = orderGateway.getPackages();

        if (!CollectionUtils.isEmpty(packageList)) {
            String oldAcceptNode = orderGateway.getOldAcceptNode();
            List<OrderHornDeviceDO> orderHornDeviceDOList = new ArrayList<>();
            for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
                String appCode = orderPackages.getApplication().getAppCode();
                String productCode = orderPackages.getApplication().getProductCode();
                List<OrderGatewayDTO.SensorDevice> hornDeviceList = orderPackages.getHornDevice();
                if (!CollectionUtils.isEmpty(hornDeviceList)) {
                    Date now = DateUtils.getNowDate();
                    for (OrderGatewayDTO.SensorDevice hornDevice : hornDeviceList) {
                        // 大喇叭
                        OrderHornDeviceDO orderHornDeviceDO = new OrderHornDeviceDO();
                        orderHornDeviceDO.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL);
                        orderHornDeviceDO.setAreaCode(hornDevice.getAreaCode());
                        orderHornDeviceDO.setAdmCode(hornDevice.getAdmCode());
                        if (!Objects.isNull(hornDevice.getStatus())) {
                            orderHornDeviceDO.setStatus(hornDevice.getStatus());
                        }
                        orderHornDeviceDO.setLatitude(hornDevice.getLatitude());
                        orderHornDeviceDO.setLongitude(hornDevice.getLongitude());
                        orderHornDeviceDO.setLocation(hornDevice.getLocation());
                        orderHornDeviceDO.setTenantId(customerCode);
                        orderHornDeviceDO.setDeviceCode(hornDevice.getDeviceCode());
                        orderHornDeviceDO.setDeviceName(hornDevice.getDevName());
                        orderHornDeviceDO.setPackageCode(orderPackages.getPackageCode());
                        orderHornDeviceDO.setOrderCode(orderCode);
                        orderHornDeviceDO.setMoveSign(moveSign);
                        orderHornDeviceDO.setCreateTime(now);
                        orderHornDeviceDO.setUpdateTime(now);
                        orderHornDeviceDO.setOrdStatus(orderGateway.getStatus());
                        orderHornDeviceDO.setIndustryCode(appCode);
                        orderHornDeviceDO.setAppVerCode(productCode);
                        // 如果订单受理节点非当前节点，不更改订单受理节点信息
                        if (BooleanUtils.isTrue(orderGateway.getHandleOrderUserInfoOnly()) && StringUtils.isNotEmpty(oldAcceptNode)) {
                            orderHornDeviceDO.setAcceptNode(oldAcceptNode);
                        } else {
                            orderHornDeviceDO.setAcceptNode(orderGateway.getAcceptNode());
                        }
                        orderHornDeviceDO.setAccessNode(hornDevice.getAccessNode());
                        orderHornDeviceDOList.add(orderHornDeviceDO);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(orderHornDeviceDOList)) {
                orderHornDeviceRepository.saveListBatch(orderHornDeviceDOList);
            }
        }
    }
}
