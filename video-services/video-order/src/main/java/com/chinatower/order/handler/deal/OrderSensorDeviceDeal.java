package com.chinatower.order.handler.deal;

import com.chinatower.common.core.domain.OrderSensorDeviceDO;
import com.chinatower.common.core.domain.OrderSensorDeviceHisDO;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.order.repository.IOrderSensorDeviceHisRepository;
import com.chinatower.order.repository.IOrderSensorDeviceRepository;
import com.chinatower.platform.constants.OrderConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName OrderSensorDeviceDeal
 * @Deacription 订单传感器设备关系通用模块
 * @Author chenmf
 * @Date 2022/1/7 10:58
 * @Version 1.0
 **/
@Component
@Slf4j
public class OrderSensorDeviceDeal extends AbstractOrderDeal {

    @Autowired
    private IOrderSensorDeviceHisRepository orderSensorDeviceHisRepository;

    @Autowired
    private IOrderSensorDeviceRepository orderSensorDeviceRepository;

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {

        String orderCode = orderGateway.getOrderCode();
        List<String> orderCodeList = Lists.newArrayList();
        orderCodeList.add(orderCode);
        List<OrderSensorDeviceDO> orderDeviceList = orderSensorDeviceRepository.getOrderDeviceByList(orderCodeList, null);
        int moveSign = orderGateway.getOrderMoveSign();
        if (!CollectionUtils.isEmpty(orderDeviceList)) {
            List<OrderSensorDeviceHisDO> orderDeviceHisList = new ArrayList<>();
            for (OrderSensorDeviceDO orderDeviceDO : orderDeviceList) {
                OrderSensorDeviceHisDO hisDO = new OrderSensorDeviceHisDO();
                BeanUtils.copyProperties(orderDeviceDO, hisDO);
                orderDeviceHisList.add(hisDO);
            }
            orderSensorDeviceHisRepository.saveListBatch(orderDeviceHisList);
            orderSensorDeviceRepository.deleteByOrderCode(orderCode);
            this.saveOrderSensorDevice(orderGateway, moveSign);
        } else {
            this.saveOrderSensorDevice(orderGateway, moveSign);
        }

        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }

    }

    /**
     * 功能描述 保存新的订单传感器关系
     *
     * @return
     * @author chenmf
     * @date 2022/1/10
     * [orderGateway, moveSign]
     */
    private void saveOrderSensorDevice(OrderGatewayDTO orderGateway, int moveSign) {

        String orderCode = orderGateway.getOrderCode();
        String customerCode = orderGateway.getCustomer().getCustomerCode();
        List<OrderGatewayDTO.OrderPackages> packageList = orderGateway.getPackages();

        if (!CollectionUtils.isEmpty(packageList)) {
            String oldAcceptNode = orderGateway.getOldAcceptNode();
            List<OrderSensorDeviceDO> orderSensorDeviceDOList = new ArrayList<>();
            for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
                String appCode = orderPackages.getApplication().getAppCode();
                String productCode = orderPackages.getApplication().getProductCode();
                List<OrderGatewayDTO.SensorDevice> sensorDeviceList = orderPackages.getSensorDevice();
                if (!CollectionUtils.isEmpty(sensorDeviceList)) {
                    for (OrderGatewayDTO.SensorDevice sensorDevice : sensorDeviceList) {
                        //传感器
                        OrderSensorDeviceDO orderSensorDeviceDO = new OrderSensorDeviceDO();
                        orderSensorDeviceDO.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL);
                        orderSensorDeviceDO.setSiteCode(sensorDevice.getSiteCode());
                        orderSensorDeviceDO.setSiteName(sensorDevice.getSiteName());
                        orderSensorDeviceDO.setAreaCode(sensorDevice.getAreaCode());
                        orderSensorDeviceDO.setCategoryCode(sensorDevice.getCategoryCode());
                        orderSensorDeviceDO.setAdmCode(sensorDevice.getAdmCode());
                        if (!Objects.isNull(sensorDevice.getStatus())) {
                            orderSensorDeviceDO.setStatus(sensorDevice.getStatus());
                        }
                        orderSensorDeviceDO.setPublicSiteCode(StringUtils.isEmpty(sensorDevice.getPublicSiteCode()) ?"":sensorDevice.getPublicSiteCode());
                        orderSensorDeviceDO.setHeight(sensorDevice.getHeight());
                        orderSensorDeviceDO.setLatitude(sensorDevice.getLatitude());
                        orderSensorDeviceDO.setLongitude(sensorDevice.getLongitude());
                        orderSensorDeviceDO.setLocation(sensorDevice.getLocation());
                        orderSensorDeviceDO.setTenantId(customerCode);
                        orderSensorDeviceDO.setDeviceCode(sensorDevice.getDeviceCode());
                        orderSensorDeviceDO.setDeviceName(sensorDevice.getDevName());
                        orderSensorDeviceDO.setPackageCode(orderPackages.getPackageCode());
                        orderSensorDeviceDO.setOrderCode(orderCode);
                        orderSensorDeviceDO.setMoveSign(moveSign);
                        orderSensorDeviceDO.setCreateTime(DateUtils.getNowDate());
                        orderSensorDeviceDO.setUpdateTime(DateUtils.getNowDate());
                        orderSensorDeviceDO.setOrdStatus(orderGateway.getStatus());
                        orderSensorDeviceDO.setIndustryCode(appCode);
                        orderSensorDeviceDO.setAppVerCode(productCode);
                        // 如果订单受理节点非当前节点，不更改订单受理节点信息
                        if (BooleanUtils.isTrue(orderGateway.getHandleOrderUserInfoOnly()) && StringUtils.isNotEmpty(oldAcceptNode)) {
                            orderSensorDeviceDO.setAcceptNode(oldAcceptNode);
                        } else {
                            orderSensorDeviceDO.setAcceptNode(orderGateway.getAcceptNode());
                        }
                        orderSensorDeviceDO.setAccessNode(sensorDevice.getAccessNode());
                        orderSensorDeviceDOList.add(orderSensorDeviceDO);
                    }
                }
            }
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(orderSensorDeviceDOList)) {
                orderSensorDeviceRepository.saveListBatch(orderSensorDeviceDOList);
            }
        }
    }
}
