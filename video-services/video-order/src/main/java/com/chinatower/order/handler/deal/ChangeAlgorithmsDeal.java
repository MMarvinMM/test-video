package com.chinatower.order.handler.deal;

import com.chinatower.common.core.domain.OrderAlgorithmsDO;
import com.chinatower.common.core.domain.OrderAlgorithmsHisDO;
import com.chinatower.common.core.domain.OrderDeviceHisDO;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.order.repository.IOrderAlgorithmsHisRepository;
import com.chinatower.order.repository.IOrderAlgorithmsRepository;
import com.chinatower.order.repository.IOrderDeviceHisRepository;
import com.chinatower.order.repository.IOrderDeviceRepository;
import com.chinatower.order.vo.AlgorithmByOrderVO;
import com.chinatower.order.vo.AlgorithmOneVO;
import com.chinatower.order.vo.AlgorithmVO;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.OrderConstants;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName ReduceAlgorithmsDeal
 * @Deacription 订单更新减少/新增的算法，
 * 删除算法配置，下发删除指令到6.35接口
 * @Author chenmf
 * @Date 2022/1/11 16:44
 * @Version 1.0
 **/
@Component
@Slf4j
public class ChangeAlgorithmsDeal extends AbstractOrderDeal {

    @Autowired
    private IOrderAlgorithmsRepository orderAlgorithmsRepository;

    @Autowired
    private IOrderAlgorithmsHisRepository orderAlgorithmsHisRepository;

    @Autowired
    private IOrderDeviceHisRepository orderDeviceHisRepository;

    @Autowired
    private IOrderDeviceRepository orderDeviceRepository;

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {
        log.info("订单更新减少/新增的算法");

        String orderCode = orderGateway.getOrderCode();
        String customerCode = orderGateway.getCustomer().getCustomerCode();
        List<String> orderCodeList = Lists.newArrayList();
        orderCodeList.add(orderCode);
        List<OrderAlgorithmsDO> orderAlgorithmsList = orderAlgorithmsRepository.getOrderAlgorithmsByList(orderCodeList, null);
        if (!CollectionUtils.isEmpty(orderAlgorithmsList)) {
            //获取迁移次数
            int moveSign = orderAlgorithmsList.get(0).getMoveSign() - 1;
            List<OrderAlgorithmsHisDO> orderAlgorithmsHisList = orderAlgorithmsHisRepository.getOrderAlgorithmsByOrderCode(orderCode, moveSign);

            //取交集
            List<OrderAlgorithmsDO> intersectAlgo = orderAlgorithmsList.stream().filter(item ->
                    orderAlgorithmsHisList.stream().map(OrderAlgorithmsHisDO::getAlgorithmCode).collect(Collectors.toList()).
                            contains(item.getAlgorithmCode())).collect(Collectors.toList());

            //需要退订的算法：删除算法并同步算法仓
            List<OrderAlgorithmsHisDO> reduceAlgoList = orderAlgorithmsHisList.stream().filter(item ->
                    !intersectAlgo.stream().map(OrderAlgorithmsDO::getAlgorithmCode).collect(Collectors.toList()).
                            contains(item.getAlgorithmCode())).collect(Collectors.toList());

            List<OrderDeviceHisDO> oldOrderDeviceList = orderDeviceHisRepository.getOldOrderDeviceByOrderCode(orderCode, moveSign);
            // 处理删除的算法
            handleReduceAlgorithmLogic(orderGateway, reduceAlgoList, oldOrderDeviceList, customerCode, orderCode);
        } else {
            //获取迁移次数
            int moveSign = orderGateway.getOrderMoveSign() - 1;
            //需要退订的算法：删除算法并同步算法仓
            List<OrderAlgorithmsHisDO> reduceAlgoList = orderAlgorithmsHisRepository.getOrderAlgorithmsByOrderCode(orderCode, moveSign);
            List<OrderDeviceHisDO> oldOrderDeviceList = orderDeviceHisRepository.getOldOrderDeviceByOrderCode(orderCode, moveSign);
            // 处理需要删除的算法
            handleReduceAlgorithmLogic(orderGateway, reduceAlgoList, oldOrderDeviceList, customerCode, orderCode);
        }

        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }
    }

    private void handleReduceAlgorithmLogic(OrderGatewayDTO orderGateway,
                                            List<OrderAlgorithmsHisDO> reduceAlgoList,
                                            List<OrderDeviceHisDO> oldOrderDeviceList,
                                            String customerCode, String orderCode) {
        Integer orderType = orderGateway.getOrderType();
        // 非精准转非精准订单，保留原有逻辑
        if (orderType == null || Objects.equals(OrderConstants.ORDER_TYPE_NON_PRECISE_TO_NON_PRECISE, orderType)) {
            handleReduceAlgorithmForNonPrecise(reduceAlgoList, oldOrderDeviceList, customerCode, orderCode);
        }
        // 非精准转精准、精准转精准订单，停用 algorithmConfigList 之外的算法配置
        else if (Objects.equals(OrderConstants.ORDER_TYPE_NON_PRECISE_TO_PRECISE, orderType)
                || Objects.equals(OrderConstants.ORDER_TYPE_PRECISE_TO_PRECISE, orderType)) {
            handleReduceAlgorithmForConfig(orderGateway, customerCode, orderCode);
        }
        // 精准转非精准订单，停用 orderPackages.algorithms 之外的算法配置
        else if (Objects.equals(OrderConstants.ORDER_TYPE_PRECISE_TO_NON_PRECISE, orderType)) {
            handleReduceAlgorithmForPackages(orderGateway, customerCode, orderCode);
        }
    }

    private void handleReduceAlgorithmForNonPrecise(List<OrderAlgorithmsHisDO> reduceAlgoList,
                                                    List<OrderDeviceHisDO> oldOrderDeviceList,
                                                    String customerCode, String orderCode) {
        List<AlgorithmVO> algorithmCodeList = new ArrayList<>();
        List<String> handleAlgorithmCodeList = Lists.newArrayList();
        for (OrderAlgorithmsHisDO hisDO : reduceAlgoList) {
            //需要退订的算 对应的集团及版本的所有设备均需要停用 所有这里循环设备取old版本设备
            for (OrderDeviceHisDO orderDeviceHisDO : oldOrderDeviceList) {
                String assembleKey = String.format("%s_%s", hisDO.getAlgorithmCode(), orderDeviceHisDO.getDeviceCode());
                if (handleAlgorithmCodeList.contains(assembleKey)) {
                    continue;
                }
                AlgorithmVO algorithmVO = new AlgorithmVO();
                algorithmVO.setAlgorithmCode(hisDO.getAlgorithmCode());
                algorithmVO.setDeviceCode(orderDeviceHisDO.getDeviceCode());
                algorithmCodeList.add(algorithmVO);
                handleAlgorithmCodeList.add(assembleKey);
            }
        }

        if (!CollectionUtils.isEmpty(algorithmCodeList)) {
            //算法停用并通知算法仓
            AlgorithmByOrderVO algorithmByOrder = new AlgorithmByOrderVO();
            algorithmByOrder.setCustomerCode(customerCode);
            algorithmByOrder.setOrderCode(orderCode);
            algorithmByOrder.setAlgorithmCodeList(algorithmCodeList);
            algorithmByOrder.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL);
            algorithmByOrder.setIndustryCode(oldOrderDeviceList.get(0).getIndustryCode());
            List<AlgorithmOneVO> areaList = orderAlgorithmsRepository.getAlgorithmAreaByOrder(algorithmByOrder);
            orderAlgorithmsRepository.updateAlgorithmStatus(DeviceConstants.ALGORITHM_STATUS_STOP, areaList);
        }
    }

    /**
     * 非精准转精准、精准转精准订单：
     * 根据 algorithmConfigList 中算法类型为摄像机(1)的设备编码、算法编码，停用 vm_algorithm_config 中不匹配的配置
     */
    private void handleReduceAlgorithmForConfig(OrderGatewayDTO orderGateway, String customerCode, String orderCode) {
        List<OrderGatewayDTO.AlgorithmConfigInfo> algorithmConfigList = orderGateway.getAlgorithmConfigList();
        // 仅处理摄像机算法配置；目标算法为空时，需将该订单的所有算法配置都停用
        Set<String> algorithmDevicePairSet = CollectionUtils.isEmpty(algorithmConfigList) ? Collections.emptySet()
                : algorithmConfigList.stream()
                        .filter(x -> Objects.equals(OrderConstants.ALGORITHM_CONFIG_TYPE_CAMERA, x.getAlgorithmType()))
                        .map(x -> buildAlgorithmDeviceKey(x.getDeviceCode(), x.getAlgorithmCode()))
                        .collect(Collectors.toSet());

        List<AlgorithmOneVO> areaList = orderAlgorithmsRepository.getAlgorithmAreaByOrder(buildBaseQuery(customerCode, orderCode));
        if (CollectionUtils.isEmpty(areaList)) {
            return;
        }
        // 停用：设备编码、算法编码不为 algorithmConfigList 中数据的配置；目标算法为空时停用全部
        List<AlgorithmOneVO> reduceAreaList = areaList.stream()
                .filter(x -> !algorithmDevicePairSet.contains(buildAlgorithmDeviceKey(x.getDeviceCode(), x.getAlgorithmCode())))
                .collect(Collectors.toList());
        orderAlgorithmsRepository.updateAlgorithmStatus(DeviceConstants.ALGORITHM_STATUS_STOP, reduceAreaList);
    }

    /**
     * 精准转非精准订单：
     * 根据 orderPackages.algorithms 订购的算法编码，停用 vm_algorithm_config 中不匹配的配置
     */
    private void handleReduceAlgorithmForPackages(OrderGatewayDTO orderGateway, String customerCode, String orderCode) {
        List<OrderGatewayDTO.OrderPackages> packageList = orderGateway.getPackages();
        // 套餐中订购的算法编码；目标算法为空时，需将该订单的所有算法配置都停用
        Set<String> packageAlgorithmCodeSet = CollectionUtils.isEmpty(packageList) ? Collections.emptySet()
                : packageList.stream()
                        .filter(x -> CollectionUtils.isNotEmpty(x.getAlgorithms()))
                        .flatMap(x -> x.getAlgorithms().stream())
                        .map(OrderGatewayDTO.Algorithm::getAlgorithmCode)
                        .collect(Collectors.toSet());

        List<AlgorithmOneVO> areaList = orderAlgorithmsRepository.getAlgorithmAreaByOrder(buildBaseQuery(customerCode, orderCode));
        if (CollectionUtils.isEmpty(areaList)) {
            return;
        }
        // 停用：算法编码不为 orderPackages.algorithms 中数据的配置；目标算法为空时停用全部
        List<AlgorithmOneVO> reduceAreaList = areaList.stream()
                .filter(x -> !packageAlgorithmCodeSet.contains(x.getAlgorithmCode()))
                .collect(Collectors.toList());
        orderAlgorithmsRepository.updateAlgorithmStatus(DeviceConstants.ALGORITHM_STATUS_STOP, reduceAreaList);
    }

    private AlgorithmByOrderVO buildBaseQuery(String customerCode, String orderCode) {
        AlgorithmByOrderVO query = new AlgorithmByOrderVO();
        query.setCustomerCode(customerCode);
        query.setOrderCode(orderCode);
        // 停用：查询当前有效的配置
        query.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL);
        return query;
    }

    private String buildAlgorithmDeviceKey(String deviceCode, String algorithmCode) {
        return deviceCode + "_" + algorithmCode;
    }
}
