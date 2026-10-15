package com.chinatower.order.handler.deal;

import com.alibaba.nacos.common.utils.MapUtils;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.order.repository.IOrderAlgorithmsRepository;
import com.chinatower.order.vo.AlgorithmByOrderVO;
import com.chinatower.order.vo.AlgorithmOneVO;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.OrderConstants;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName DeviceAlgorithmsDeal
 * @Deacription 订单停用，停用算法配置
 * 订单启用，启用算法配置
 * @Author chenmf
 * @Date 2022/1/11 16:44
 * @Version 1.0
 **/
@Component
@Slf4j
public class EnableAlgorithmsDeal extends AbstractOrderDeal {

    @Autowired
    private IOrderAlgorithmsRepository orderAlgorithmsRepository;

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {

        String orderCode = orderGateway.getOrderCode();
        String customerCode = orderGateway.getCustomer().getCustomerCode();
        int status = orderGateway.getStatus();
        Integer orderType = orderGateway.getOrderType();

        // 非精准转非精准订单，保留原有逻辑
        if (orderType == null || Objects.equals(OrderConstants.ORDER_TYPE_NON_PRECISE_TO_NON_PRECISE, orderType)) {
            dealNonPreciseOrder(orderGateway, orderCode, customerCode, status);
        }
        // 非精准转精准、精准转精准订单，按算法配置信息停用/启用
        else if (Objects.equals(OrderConstants.ORDER_TYPE_NON_PRECISE_TO_PRECISE, orderType)
                || Objects.equals(OrderConstants.ORDER_TYPE_PRECISE_TO_PRECISE, orderType)) {
            dealAlgorithmConfigOrder(orderGateway, orderCode, customerCode, status);
        }
        // 精准转非精准订单，按套餐订购算法停用/启用
        else if (Objects.equals(OrderConstants.ORDER_TYPE_PRECISE_TO_NON_PRECISE, orderType)) {
            dealOrderPackagesOrder(orderGateway, orderCode, customerCode, status);
        }

        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }
    }

    /**
     * 非精准转非精准订单，原有算法停用/启用逻辑
     */
    private void dealNonPreciseOrder(OrderGatewayDTO orderGateway, String orderCode, String customerCode, int status) {
        List<OrderGatewayDTO.OrderPackages> packageList = orderGateway.getPackages();
        if (!CollectionUtils.isEmpty(packageList)) {
            Map<String, Set<String>> industryDeviceMap = Maps.newHashMap();
            for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
                List<OrderGatewayDTO.Device> deviceList = orderPackages.getDevices();
                if (!CollectionUtils.isEmpty(deviceList)) {
                    String industryCode = orderPackages.getApplication().getAppCode();
                    Set<String> deviceCodeSet = deviceList.stream().map(OrderGatewayDTO.Device::getDeviceCode).collect(Collectors.toSet());
                    if (industryDeviceMap.containsKey(industryCode)) {
                        industryDeviceMap.get(industryCode).addAll(deviceCodeSet);
                    } else {
                        industryDeviceMap.put(industryCode, deviceCodeSet);
                    }

                } else {
                    log.error("订单{}对应的设备信息为空", orderCode);
                }
            }
            if (MapUtils.isNotEmpty(industryDeviceMap)) {
                List<String> industryCodeList = industryDeviceMap.keySet().stream().distinct().collect(Collectors.toList());
                List<String> deviceCodeList = industryDeviceMap.values().stream().flatMap(Set::stream).distinct().collect(Collectors.toList());

                // 算法停用/启用并通知算法仓
                AlgorithmByOrderVO algorithmByOrder = new AlgorithmByOrderVO();
                algorithmByOrder.setCustomerCode(customerCode);
                algorithmByOrder.setOrderCode(orderCode);
                algorithmByOrder.setDeviceCodeList(deviceCodeList);
                algorithmByOrder.setIndustryCodeList(industryCodeList);
                if (Objects.equals(OrderConstants.STATUS_FOR_ENABLE, status)) {
                    //启用 需要将delete_flag改为0
                    algorithmByOrder.setDeleteFlag(OrderConstants.DELETE_FLAG_EXCEPTION);
                } else if (Objects.equals(OrderConstants.STATUS_FOR_DEACTIVATE, status)) {
                    //停用
                    algorithmByOrder.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL);
                }
                List<AlgorithmOneVO> areaList = orderAlgorithmsRepository.getAlgorithmAreaByOrder(algorithmByOrder);

                // 匹配到需要更改的配置id列表
                List<String> configIdList = areaList.stream().filter(x -> industryDeviceMap.containsKey(x.getIndustryCode())
                                && industryDeviceMap.get(x.getIndustryCode()).contains(x.getDeviceCode()))
                        .map(AlgorithmOneVO::getConfigId).distinct().collect(Collectors.toList());

                if (Objects.equals(OrderConstants.STATUS_FOR_ENABLE, status)) {
                    // 启用
                    orderAlgorithmsRepository.updateAlgorithmStatusByConfigId(DeviceConstants.ALGORITHM_STATUS_ENABLE, configIdList);
                } else if (Objects.equals(OrderConstants.STATUS_FOR_DEACTIVATE, status)) {
                    // 停用
                    orderAlgorithmsRepository.updateAlgorithmStatusByConfigId(DeviceConstants.ALGORITHM_STATUS_STOP, configIdList);
                }
            }
        }
    }

    /**
     * 非精准转精准、精准转精准订单：
     * 根据 algorithmConfigList 中算法类型为摄像机(1)的设备编码、算法编码，匹配 vm_algorithm_config 配置停用/启用
     */
    private void dealAlgorithmConfigOrder(OrderGatewayDTO orderGateway, String orderCode, String customerCode, int status) {
        List<OrderGatewayDTO.AlgorithmConfigInfo> algorithmConfigList = orderGateway.getAlgorithmConfigList();
        // 仅处理摄像机算法配置；目标算法为空时，停用则停用全部，启用则无配置可启用
        Set<String> algorithmDevicePairSet = CollectionUtils.isEmpty(algorithmConfigList) ? Collections.emptySet()
                : algorithmConfigList.stream()
                        .filter(x -> Objects.equals(OrderConstants.ALGORITHM_CONFIG_TYPE_CAMERA, x.getAlgorithmType()))
                        .map(x -> buildAlgorithmDeviceKey(x.getDeviceCode(), x.getAlgorithmCode()))
                        .collect(Collectors.toSet());

        List<AlgorithmOneVO> areaList = orderAlgorithmsRepository.getAlgorithmAreaByOrder(buildBaseQuery(customerCode, orderCode, status));
        if (CollectionUtils.isEmpty(areaList)) {
            return;
        }

        if (Objects.equals(OrderConstants.STATUS_FOR_ENABLE, status)) {
            // 启用：设备编码、算法编码为 algorithmConfigList 中数据的配置；目标算法为空时无配置可启用
            List<String> configIdList = areaList.stream()
                    .filter(x -> algorithmDevicePairSet.contains(buildAlgorithmDeviceKey(x.getDeviceCode(), x.getAlgorithmCode())))
                    .map(AlgorithmOneVO::getConfigId).distinct().collect(Collectors.toList());
            orderAlgorithmsRepository.updateAlgorithmStatusByConfigId(DeviceConstants.ALGORITHM_STATUS_ENABLE, configIdList);
        } else if (Objects.equals(OrderConstants.STATUS_FOR_DEACTIVATE, status)) {
            // 停用：设备编码、算法编码不为 algorithmConfigList 中数据的配置；目标算法为空时停用全部
            List<String> configIdList = areaList.stream()
                    .filter(x -> !algorithmDevicePairSet.contains(buildAlgorithmDeviceKey(x.getDeviceCode(), x.getAlgorithmCode())))
                    .map(AlgorithmOneVO::getConfigId).distinct().collect(Collectors.toList());
            orderAlgorithmsRepository.updateAlgorithmStatusByConfigId(DeviceConstants.ALGORITHM_STATUS_STOP, configIdList);
        }
    }

    /**
     * 精准转非精准订单：
     * 根据 orderPackages.algorithms 订购的算法编码，匹配 vm_algorithm_config 配置停用/启用
     */
    private void dealOrderPackagesOrder(OrderGatewayDTO orderGateway, String orderCode, String customerCode, int status) {
        List<OrderGatewayDTO.OrderPackages> packageList = orderGateway.getPackages();
        // 套餐中订购的算法编码；目标算法为空时，停用则停用全部，启用则无配置可启用
        Set<String> packageAlgorithmCodeSet = CollectionUtils.isEmpty(packageList) ? Collections.emptySet()
                : packageList.stream()
                        .filter(x -> CollectionUtils.isNotEmpty(x.getAlgorithms()))
                        .flatMap(x -> x.getAlgorithms().stream())
                        .map(OrderGatewayDTO.Algorithm::getAlgorithmCode)
                        .collect(Collectors.toSet());

        List<AlgorithmOneVO> areaList = orderAlgorithmsRepository.getAlgorithmAreaByOrder(buildBaseQuery(customerCode, orderCode, status));
        if (CollectionUtils.isEmpty(areaList)) {
            return;
        }

        if (Objects.equals(OrderConstants.STATUS_FOR_ENABLE, status)) {
            // 启用：算法编码为 orderPackages.algorithms 中数据的配置；目标算法为空时无配置可启用
            List<String> configIdList = areaList.stream()
                    .filter(x -> packageAlgorithmCodeSet.contains(x.getAlgorithmCode()))
                    .map(AlgorithmOneVO::getConfigId).distinct().collect(Collectors.toList());
            orderAlgorithmsRepository.updateAlgorithmStatusByConfigId(DeviceConstants.ALGORITHM_STATUS_ENABLE, configIdList);
        } else if (Objects.equals(OrderConstants.STATUS_FOR_DEACTIVATE, status)) {
            // 停用：算法编码不为 orderPackages.algorithms 中数据的配置；目标算法为空时停用全部
            List<String> configIdList = areaList.stream()
                    .filter(x -> !packageAlgorithmCodeSet.contains(x.getAlgorithmCode()))
                    .map(AlgorithmOneVO::getConfigId).distinct().collect(Collectors.toList());
            orderAlgorithmsRepository.updateAlgorithmStatusByConfigId(DeviceConstants.ALGORITHM_STATUS_STOP, configIdList);
        }
    }

    private AlgorithmByOrderVO buildBaseQuery(String customerCode, String orderCode, int status) {
        AlgorithmByOrderVO query = new AlgorithmByOrderVO();
        query.setCustomerCode(customerCode);
        query.setOrderCode(orderCode);
        if (Objects.equals(OrderConstants.STATUS_FOR_ENABLE, status)) {
            // 启用：查询已停用的配置，启用后 delete_flag 改为 0
            query.setDeleteFlag(OrderConstants.DELETE_FLAG_EXCEPTION);
        } else if (Objects.equals(OrderConstants.STATUS_FOR_DEACTIVATE, status)) {
            // 停用：查询当前有效的配置
            query.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL);
        }
        return query;
    }

    private String buildAlgorithmDeviceKey(String deviceCode, String algorithmCode) {
        return deviceCode + "_" + algorithmCode;
    }
}