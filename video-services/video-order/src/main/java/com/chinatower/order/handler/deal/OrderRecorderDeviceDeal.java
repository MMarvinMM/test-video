package com.chinatower.order.handler.deal;

import com.chinatower.common.core.domain.*;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.order.repository.IOrderRecorderDeviceHisRepository;
import com.chinatower.order.repository.IOrderRecorderDeviceRepository;
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
public class OrderRecorderDeviceDeal extends AbstractOrderDeal {

    @Autowired
    private IOrderRecorderDeviceHisRepository orderRecorderDeviceHisRepository;

    @Autowired
    private IOrderRecorderDeviceRepository orderRecorderDeviceRepository;

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {
        log.info("OrderRecorderDeviceDeal 订购处理开始");
        String orderCode = orderGateway.getOrderCode();
        List<String> orderCodeList = Lists.newArrayList();
        orderCodeList.add(orderCode);
        List<OrderRecorderDeviceDO> orderDeviceList = orderRecorderDeviceRepository.getOrderDeviceByList(orderCodeList, null);
        int moveSign = orderGateway.getOrderMoveSign();
        if (!CollectionUtils.isEmpty(orderDeviceList)) {
            List<OrderRecorderDeviceHisDO> orderDeviceHisList = new ArrayList<>();
            for (OrderRecorderDeviceDO orderDeviceDO : orderDeviceList) {
                OrderRecorderDeviceHisDO hisDO = new OrderRecorderDeviceHisDO();
                BeanUtils.copyProperties(orderDeviceDO, hisDO);
                orderDeviceHisList.add(hisDO);
            }
            orderRecorderDeviceHisRepository.saveListBatch(orderDeviceHisList);
            orderRecorderDeviceRepository.deleteByOrderCode(orderCode);
            this.saveOrderRecorderDevice(orderGateway, moveSign);
        } else {
            this.saveOrderRecorderDevice(orderGateway, moveSign);
        }
        log.info("OrderRecorderDeviceDeal 订购处理结束");
        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }

    }

    private void saveOrderRecorderDevice(OrderGatewayDTO orderGateway, int moveSign) {

        String orderCode = orderGateway.getOrderCode();
        String customerCode = orderGateway.getCustomer().getCustomerCode();
        List<OrderGatewayDTO.OrderPackages> packageList = orderGateway.getPackages();

        if (!CollectionUtils.isEmpty(packageList)) {
            String oldAcceptNode = orderGateway.getOldAcceptNode();
            List<OrderRecorderDeviceDO> orderSensorDeviceDOList = new ArrayList<>();
            for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
                String appCode = orderPackages.getApplication().getAppCode();
                String productCode = orderPackages.getApplication().getProductCode();
                List<OrderGatewayDTO.Device> recorderDeviceList = orderPackages.getRecorderDevice();
                if (!CollectionUtils.isEmpty(recorderDeviceList)) {
                    for (OrderGatewayDTO.Device recorderDevice : recorderDeviceList) {
                        //传感器
                        OrderRecorderDeviceDO orderDeviceDO = new OrderRecorderDeviceDO();
                        orderDeviceDO.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL);
                        orderDeviceDO.setAreaCode(recorderDevice.getAreaCode());
                        orderDeviceDO.setAdmCode(recorderDevice.getAdmCode());
                        if (!Objects.isNull(recorderDevice.getStatus())) {
                            orderDeviceDO.setStatus(recorderDevice.getStatus());
                        }
                        orderDeviceDO.setLatitude(recorderDevice.getLatitude());
                        orderDeviceDO.setLongitude(recorderDevice.getLongitude());
                        orderDeviceDO.setLocation(recorderDevice.getLocation());
                        orderDeviceDO.setTenantId(customerCode);
                        orderDeviceDO.setDeviceCode(recorderDevice.getDeviceCode());
                        orderDeviceDO.setDeviceName(recorderDevice.getDevName());
                        orderDeviceDO.setPackageCode(orderPackages.getPackageCode());
                        orderDeviceDO.setOrderCode(orderCode);
                        orderDeviceDO.setMoveSign(moveSign);
                        orderDeviceDO.setCreateTime(DateUtils.getNowDate());
                        orderDeviceDO.setUpdateTime(DateUtils.getNowDate());
                        orderDeviceDO.setOrdStatus(orderGateway.getStatus());
                        orderDeviceDO.setIndustryCode(appCode);
                        orderDeviceDO.setAppVerCode(productCode);
                        // 如果订单受理节点非当前节点，不更改订单受理节点信息
                        if (BooleanUtils.isTrue(orderGateway.getHandleOrderUserInfoOnly()) && StringUtils.isNotEmpty(oldAcceptNode)) {
                            orderDeviceDO.setAcceptNode(oldAcceptNode);
                        } else {
                            orderDeviceDO.setAcceptNode(orderGateway.getAcceptNode());
                        }
                        orderDeviceDO.setAccessNode(recorderDevice.getAccessNode());
                        orderSensorDeviceDOList.add(orderDeviceDO);
                    }
                }
            }
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(orderSensorDeviceDOList)) {
                orderRecorderDeviceRepository.saveListBatch(orderSensorDeviceDOList);
            }
        }
    }
}
