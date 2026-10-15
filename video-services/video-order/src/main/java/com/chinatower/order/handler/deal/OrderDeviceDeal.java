package com.chinatower.order.handler.deal;

import com.chinatower.common.core.domain.OrderDeviceDO;
import com.chinatower.common.core.domain.OrderDeviceHisDO;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.order.repository.IOrderDeviceHisRepository;
import com.chinatower.order.repository.IOrderDeviceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName orderRelationshipDeal
 * @Deacription 订单设备通道关系通用模块
 * @Author chenmf
 * @Date 2022/1/7 10:58
 * @Version 1.0
 **/
@Component
@Slf4j
public class OrderDeviceDeal extends AbstractOrderDeal{

    @Autowired
    private IOrderDeviceHisRepository orderDeviceHisRepository;

    @Autowired
    private IOrderDeviceRepository orderDeviceRepository;

    private static final int BATCH_SIZE = 2000;

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {

        log.info("OrderDeviceDeal 订购处理开始");

        String orderCode = orderGateway.getOrderCode();
        List<OrderDeviceDO> orderDeviceList = orderDeviceRepository.getOrderDeviceByOrderCode(orderCode, null);
        int moveSign = orderGateway.getOrderMoveSign();
        if (!CollectionUtils.isEmpty(orderDeviceList)) {
            List<OrderDeviceHisDO> orderDeviceHisList = new ArrayList<>();
            for (OrderDeviceDO orderDeviceDO : orderDeviceList) {
                OrderDeviceHisDO hisDO = new OrderDeviceHisDO();
                BeanUtils.copyProperties(orderDeviceDO, hisDO);
                orderDeviceHisList.add(hisDO);
            }
            log.info("OrderDeviceDeal 订购处理开始 1.1");
//            orderDeviceHisRepository.saveListBatch(orderDeviceHisList);
//            Lists.partition(orderDeviceHisList, 5000).forEach(subList -> {
//                orderDeviceHisRepository.batchInsert(subList);
//            });

            if (orderDeviceHisList.size() <= BATCH_SIZE) {
                orderDeviceHisRepository.batchInsert(orderDeviceHisList);
            }
            else {
                List<List<OrderDeviceHisDO>> partList = Lists.partition(orderDeviceHisList, BATCH_SIZE);
                ThreadParallelUtil.run(partList, part -> orderDeviceHisRepository.batchInsert(part),
                        ThreadParallelUtil.ConfigFactory.ORDER_DEVICE_DEAL);
            }
            log.info("OrderDeviceDeal 订购处理开始 1.2");
            orderDeviceRepository.deleteByOrderCodeNewTransactional(orderCode);
            log.info("OrderDeviceDeal 订购处理开始 1.3");
            this.saveOrderDevice(orderGateway, moveSign);
        }
        else {
            this.saveOrderDevice(orderGateway, moveSign);
        }

        log.info("OrderDeviceDeal 订购处理结束");

        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }

    }

    /**
     * 功能描述 保存新的订单设备通道关系
     * @author chenmf
     * @date 2022/1/10
     * [orderGateway, moveSign]
     * @return
     */
    private void saveOrderDevice(OrderGatewayDTO orderGateway, int moveSign) {
        String orderCode = orderGateway.getOrderCode();
        String customerCode = orderGateway.getCustomer().getCustomerCode();
        List<OrderGatewayDTO.OrderPackages> packageList = orderGateway.getPackages();
        if (!CollectionUtils.isEmpty(packageList)) {
            String oldAcceptNode = orderGateway.getOldAcceptNode();
            List<OrderDeviceDO> orderDeviceDOList = new ArrayList<>();
            for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
                String appCode = orderPackages.getApplication().getAppCode();
                String productCode = orderPackages.getApplication().getProductCode();
                List<OrderGatewayDTO.Device> deviceList = orderPackages.getDevices();
                if (!CollectionUtils.isEmpty(deviceList)) {
                    for (OrderGatewayDTO.Device device : deviceList) {
                        OrderDeviceDO orderDeviceDO = new OrderDeviceDO();
                        orderDeviceDO.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL);
                        orderDeviceDO.setTenantId(customerCode);
                        orderDeviceDO.setDeviceCode(device.getDeviceCode());
                        orderDeviceDO.setDeviceName(device.getDevName());
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
                        orderDeviceDO.setAccessNode(device.getAccessNode());
                        orderDeviceDOList.add(orderDeviceDO);
                    }
                }
            }

            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(orderDeviceDOList)) {
                if (orderDeviceDOList.size() <= BATCH_SIZE) {
                    orderDeviceRepository.batchInsert(orderDeviceDOList);
                }
                else {
                    List<List<OrderDeviceDO>> partList = Lists.partition(orderDeviceDOList, BATCH_SIZE);
                    ThreadParallelUtil.run(partList, part -> orderDeviceRepository.batchInsert(part),
                            ThreadParallelUtil.ConfigFactory.ORDER_DEVICE_DEAL);
                }
            }
        }
    }
}
