package com.chinatower.order.handler.deal;

import com.chinatower.common.core.domain.OrderEnergyDeviceDO;
import com.chinatower.common.core.domain.OrderEnergyDeviceHisDO;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.order.repository.IOrderEnergyDeviceHisRepository;
import com.chinatower.order.repository.IOrderEnergyDeviceRepository;
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
public class OrderEnergyDeviceDeal extends AbstractOrderDeal {

    @Autowired
    private IOrderEnergyDeviceHisRepository orderEnergyDeviceHisRepository;

    @Autowired
    private IOrderEnergyDeviceRepository orderEnergyDeviceRepository;

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {
        log.info("OrderEnergyDeviceDeal 订购处理开始");
        String orderCode = orderGateway.getOrderCode();
        List<String> orderCodeList = Lists.newArrayList();
        orderCodeList.add(orderCode);
        List<OrderEnergyDeviceDO> orderDeviceList = orderEnergyDeviceRepository.getOrderDeviceByList(orderCodeList, null);
        int moveSign = orderGateway.getOrderMoveSign();
        if (!CollectionUtils.isEmpty(orderDeviceList)) {
            List<OrderEnergyDeviceHisDO> orderDeviceHisList = new ArrayList<>();
            for (OrderEnergyDeviceDO orderDeviceDO : orderDeviceList) {
                OrderEnergyDeviceHisDO hisDO = new OrderEnergyDeviceHisDO();
                BeanUtils.copyProperties(orderDeviceDO, hisDO);
                orderDeviceHisList.add(hisDO);
            }
            orderEnergyDeviceHisRepository.saveListBatch(orderDeviceHisList);
            orderEnergyDeviceRepository.deleteByOrderCode(orderCode);
            this.saveOrderEnergyDevice(orderGateway, moveSign);
        } else {
            this.saveOrderEnergyDevice(orderGateway, moveSign);
        }
        log.info("OrderEnergyDeviceDeal 订购处理结束");
        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }

    }

    private void saveOrderEnergyDevice(OrderGatewayDTO orderGateway, int moveSign) {

        String orderCode = orderGateway.getOrderCode();
        String customerCode = orderGateway.getCustomer().getCustomerCode();
        List<OrderGatewayDTO.OrderPackages> packageList = orderGateway.getPackages();

        if (!CollectionUtils.isEmpty(packageList)) {
            String oldAcceptNode = orderGateway.getOldAcceptNode();
            List<OrderEnergyDeviceDO> orderEnergyDeviceDOList = new ArrayList<>();
            for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
                String appCode = orderPackages.getApplication().getAppCode();
                String productCode = orderPackages.getApplication().getProductCode();
                List<OrderGatewayDTO.EnergyDevice> energyDeviceList = orderPackages.getEnergyDevice();
                if (!CollectionUtils.isEmpty(energyDeviceList)) {
                    for (OrderGatewayDTO.EnergyDevice energyDevice : energyDeviceList) {
                        // 能源设备
                        OrderEnergyDeviceDO orderDeviceDO = new OrderEnergyDeviceDO();
                        orderDeviceDO.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL);
                        orderDeviceDO.setDeviceCode(energyDevice.getDeviceCode());
                        orderDeviceDO.setDeviceName(energyDevice.getDevName());
                        if (!Objects.isNull(energyDevice.getStatus())) {
                            orderDeviceDO.setStatus(energyDevice.getStatus());
                        }
                        if (Objects.equals(energyDevice.getCategoryCode(), 10)) {
                            energyDevice.setCategoryCode(4);
                        } else if (Objects.equals(energyDevice.getCategoryCode(), 11)) {
                            energyDevice.setCategoryCode(8);
                        }
                        orderDeviceDO.setCategoryCode(energyDevice.getCategoryCode());
                        orderDeviceDO.setSiteCode(energyDevice.getSiteCode());
                        orderDeviceDO.setSiteName(energyDevice.getSiteName());
                        orderDeviceDO.setProvinceCode(energyDevice.getProvinceCode());
                        orderDeviceDO.setCityCode(energyDevice.getCityCode());
                        orderDeviceDO.setCountyCode(energyDevice.getCountyCode());
                        orderDeviceDO.setCrewProvinceCode(energyDevice.getCrewProvinceCode());
                        orderDeviceDO.setCrewCityCode(energyDevice.getCrewCityCode());
                        orderDeviceDO.setCrewCountyCode(energyDevice.getCrewCountyCode());
                        orderDeviceDO.setTenantId(customerCode);

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
                        orderDeviceDO.setAccessNode(energyDevice.getAccessNode());
                        orderEnergyDeviceDOList.add(orderDeviceDO);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(orderEnergyDeviceDOList)) {
                orderEnergyDeviceRepository.saveListBatch(orderEnergyDeviceDOList);
            }
        }
    }
}
