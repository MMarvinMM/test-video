package com.chinatower.order.handler.deal;

import com.chinatower.common.core.domain.OrderUavNestDeviceDO;
import com.chinatower.common.core.domain.OrderUavNestDeviceHisDO;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.order.repository.IOrderUavNestDeviceHisRepository;
import com.chinatower.order.repository.IOrderUavNestDeviceRepository;
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
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class OrderUavNestDeviceDeal extends AbstractOrderDeal {

    @Autowired
    private IOrderUavNestDeviceHisRepository orderUavNestDeviceHisRepository;

    @Autowired
    private IOrderUavNestDeviceRepository orderUavNestDeviceRepository;

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {
        log.info("OrderUavNestDeviceDeal 订购处理开始");
        String orderCode = orderGateway.getOrderCode();
        List<String> orderCodeList = Lists.newArrayList();
        orderCodeList.add(orderCode);
        List<OrderUavNestDeviceDO> orderDeviceList = orderUavNestDeviceRepository.getOrderDeviceByList(orderCodeList, null);
        int moveSign = orderGateway.getOrderMoveSign();
        if (!CollectionUtils.isEmpty(orderDeviceList)) {
            List<OrderUavNestDeviceHisDO> orderDeviceHisList = new ArrayList<>();
            for (OrderUavNestDeviceDO orderDeviceDO : orderDeviceList) {
                OrderUavNestDeviceHisDO hisDO = new OrderUavNestDeviceHisDO();
                BeanUtils.copyProperties(orderDeviceDO, hisDO);
                orderDeviceHisList.add(hisDO);
            }
            orderUavNestDeviceHisRepository.saveListBatch(orderDeviceHisList);
            orderUavNestDeviceRepository.deleteByOrderCode(orderCode);
            this.saveOrderUavNestDevice(orderGateway, moveSign);
        } else {
            this.saveOrderUavNestDevice(orderGateway, moveSign);
        }
        log.info("OrderUavNestDeviceDeal 订购处理结束");
        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }

    }

    private void saveOrderUavNestDevice(OrderGatewayDTO orderGateway, int moveSign) {

        String orderCode = orderGateway.getOrderCode();
        String customerCode = orderGateway.getCustomer().getCustomerCode();
        List<OrderGatewayDTO.OrderPackages> packageList = orderGateway.getPackages();

        if (!CollectionUtils.isEmpty(packageList)) {
            String oldAcceptNode = orderGateway.getOldAcceptNode();
            List<OrderUavNestDeviceDO> orderDeviceDOList = new ArrayList<>();
            for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
                String appCode = orderPackages.getApplication().getAppCode();
                String productCode = orderPackages.getApplication().getProductCode();
                List<OrderGatewayDTO.Device> uavDeviceList = orderPackages.getNestDevice();
                if (!CollectionUtils.isEmpty(uavDeviceList)) {
                    for (OrderGatewayDTO.Device uavDevice : uavDeviceList) {
                        //传感器
                        OrderUavNestDeviceDO orderUavDeviceDO = new OrderUavNestDeviceDO();
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
                        orderDeviceDOList.add(orderUavDeviceDO);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(orderDeviceDOList)) {
                orderUavNestDeviceRepository.saveListBatch(orderDeviceDOList);
            }
        }
    }
}
