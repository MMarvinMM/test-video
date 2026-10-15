package com.chinatower.order.handler.deal;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.domain.OrderPushRecordDO;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.core.dto.OrderRelationshipDTO;
import com.chinatower.common.core.utils.CompletableFutureHelper;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.order.api.IOrderRemoteService;
import com.chinatower.order.mapper.OrderPushRecordMapper;
import com.chinatower.order.repository.IOrderCustomerRepository;
import com.chinatower.platform.common.utils.CommonUtil;
import com.chinatower.platform.utils.RemoteNodeUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class OrderLocationSyncDeal extends AbstractOrderDeal {

    @Autowired
    private IOrderCustomerRepository iOrderCustomerRepository;
    @Autowired
    private OrderPushRecordMapper orderPushRecordMapper;

    private static final ExecutorService THREAD_POOL_PUSH = Executors.newFixedThreadPool(20);

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {
        if (BooleanUtils.isNotTrue(orderGateway.getIsCenterButNotAcceptNode())) {
            log.info("OrderLocationSyncDeal orderRelationshipDeal 当前非中心节点，不进行订单转发处理");
            return;
        }
        log.info("OrderLocationSyncDeal orderRelationshipDeal 处理属地化推送逻辑");

        //异步处理属地化订购推送
        CompletableFutureHelper.orTimeout(
                CompletableFuture.runAsync(() -> this.handlePushLogic(orderGateway), THREAD_POOL_PUSH)
                        .exceptionally(e -> {
                            log.error("属地化订购推送失败, e :", e);
                            return null;
                        }),
                30, TimeUnit.SECONDS).exceptionally(e -> {
                    log.error("属地化订购推送超时, e:", e);
                    return null;
                }
        );

        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }
    }

    private void handlePushLogic(OrderGatewayDTO orderGateway) {
        String locationCode = orderGateway.getAcceptNode();
        if (StringUtils.isEmpty(locationCode)) {
            log.error("OrderLocationSyncDeal 受理节点为空，不处理订购推送");
            return;
        }

        // 将无人机、喇叭数据回填到device和sensorDevice里面
        handlePushOrderGateWayInfo(orderGateway);

        List<String> pushLocationCodeList = Lists.newArrayList(locationCode);

        // 查询当前节点所有上级节点【除去中心节点】，循环推送订购信息，推送失败记录订购推送表，推送成功更新状态 level = 0
        List<String> parentLocationCodeList = iOrderCustomerRepository.getParentCodeListByLocationCode(locationCode);
        if (CollectionUtils.isNotEmpty(parentLocationCodeList)) {
            pushLocationCodeList.addAll(parentLocationCodeList);
        }
        log.info("OrderLocationSyncDeal orderRelationshipDeal pushLocationCodeList:{}", JSON.toJSONString(pushLocationCodeList));
        // 需要新增推送记录的数据
        List<OrderPushRecordDO> insertOrderPushRecordList = Lists.newArrayList();
        Date pushTime = new Date();
        for (String pushLocationCode : pushLocationCodeList) {
            // 推送属地化节点逻辑
            int pushStatus = 1;
            String failReason = "";
            try {
                Result result = push2LocationLogic(pushLocationCode, orderGateway);
                log.info("OrderLocationSyncDeal orderRelationshipDeal push2LocationLogic result:{}", JSON.toJSONString(result));
                if (result.getCode() != ResultCode.SUCCESS.getCode()) {
                    pushStatus = 2;
                    failReason = result.getMsg();
                }
            } catch (Exception e) {
                log.error("OrderLocationSyncDeal push2LocationLogic error, e:", e);
                pushStatus = 2;
                failReason = e.getMessage();
            }
            insertOrderPushRecordList.add(assembleInsertOrderPushDO(pushLocationCode, orderGateway, pushTime,
                    pushStatus, failReason));
        }
        // 处理新增推送记录逻辑
        if (CollectionUtils.isNotEmpty(insertOrderPushRecordList)) {
            orderPushRecordMapper.insertBatch(insertOrderPushRecordList);
        }
    }


    private void handlePushOrderGateWayInfo(OrderGatewayDTO orderGateway) {
        if (!org.springframework.util.CollectionUtils.isEmpty(orderGateway.getPackages())) {
            List<OrderGatewayDTO.OrderPackages> newPackageList = Lists.newArrayList();
            for (OrderGatewayDTO.OrderPackages orderPackage : orderGateway.getPackages()) {
                // 将OrderGatewayDTO.UavDevice中的设备回填到摄像机列表中
                List<OrderGatewayDTO.Device> uavDeviceList = orderPackage.getUavDevice();
                if (CollectionUtils.isNotEmpty(uavDeviceList)) {
                    List<OrderGatewayDTO.Device> deviceList = orderPackage.getDevices();
                    if (CollectionUtils.isNotEmpty(deviceList)) {
                        deviceList.addAll(uavDeviceList);
                        orderPackage.setDevices(deviceList);
                    } else {
                        orderPackage.setDevices(uavDeviceList);
                    }
                    orderPackage.setUavDevice(null);
                }

                // 将OrderGatewayDTO.HornDevice中的设备回填到物联设备列表中
                List<OrderGatewayDTO.SensorDevice> hornDeviceList = orderPackage.getHornDevice();
                if (!org.springframework.util.CollectionUtils.isEmpty(hornDeviceList)) {
                    List<OrderGatewayDTO.SensorDevice> sensorDeviceList = orderPackage.getSensorDevice();
                    if (CollectionUtils.isNotEmpty(sensorDeviceList)) {
                        sensorDeviceList.addAll(hornDeviceList);
                        orderPackage.setSensorDevice(sensorDeviceList);
                    } else {
                        orderPackage.setSensorDevice(hornDeviceList);
                    }
                    orderPackage.setHornDevice(null);
                }

                // 将OrderGatewayDTO.RecorderDevice中的设备回填到摄像机设备列表中
                List<OrderGatewayDTO.Device> recorderDeviceList = orderPackage.getRecorderDevice();
                if (CollectionUtils.isNotEmpty(recorderDeviceList)) {
                    List<OrderGatewayDTO.Device> deviceList = orderPackage.getDevices();
                    if (CollectionUtils.isNotEmpty(deviceList)) {
                        deviceList.addAll(recorderDeviceList);
                        orderPackage.setDevices(deviceList);
                    } else {
                        orderPackage.setDevices(recorderDeviceList);
                    }
                    orderPackage.setRecorderDevice(null);
                }

                newPackageList.add(orderPackage);
            }
            orderGateway.setPackages(newPackageList);
        }
    }

    private Result push2LocationLogic(String toPlatformCode, OrderGatewayDTO orderGateway) {
        OrderRelationshipDTO requestParam = new OrderRelationshipDTO();
        requestParam.setEventId(CommonUtil.getUuid());
        requestParam.setData(orderGateway);

        //远程调用
        return RemoteNodeUtils.create(toPlatformCode)
                .getFeign(IOrderRemoteService.class, RemoteNodeUtils.RemoteUrlEenum.CASCADE.getCode())
                .orderRelationship(requestParam);
    }

    private OrderPushRecordDO assembleInsertOrderPushDO(String pushLocationCode, OrderGatewayDTO orderGateway,
                                                        Date pushTime, Integer pushStatus, String failReason) {
        OrderPushRecordDO result = new OrderPushRecordDO();
        result.setOrderCode(orderGateway.getOrderCode());
        result.setOrderTime(orderGateway.getOrderTime());
        result.setOrdStatus(orderGateway.getStatus());
        result.setPushTime(pushTime);
        result.setPushStatus(pushStatus);
        result.setPushFailReason(failReason);
        result.setAcceptanceNode(pushLocationCode);
        result.setTenantId(orderGateway.getCustomer().getCustomerCode());
        result.setDeleteFlag(0);
        result.setCreateTime(pushTime);
        return result;
    }
}
