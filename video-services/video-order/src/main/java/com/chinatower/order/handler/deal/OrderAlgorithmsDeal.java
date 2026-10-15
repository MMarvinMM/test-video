package com.chinatower.order.handler.deal;

import com.chinatower.common.core.domain.OrderAlgorithmConfigDO;
import com.chinatower.common.core.domain.OrderAlgorithmsDO;
import com.chinatower.common.core.domain.OrderAlgorithmsHisDO;
import com.chinatower.common.core.domain.OrderDeviceHisDO;
import com.chinatower.common.core.domain.OrderUavDeviceHisDO;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.order.repository.IOrderAlgorithmConfigRepository;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.order.repository.IOrderAlgorithmsHisRepository;
import com.chinatower.order.repository.IOrderAlgorithmsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ClassName OrderCustomerDeal
 * @Deacription 订单算法关系通用模块
 * @Author chenmf
 * @Date 2022/1/7 10:58
 * @Version 1.0
 **/
@Component
@Slf4j
public class OrderAlgorithmsDeal extends AbstractOrderDeal{

    @Autowired
    private IOrderAlgorithmsHisRepository orderAlgorithmsHisRepository;

    @Autowired
    private IOrderAlgorithmsRepository orderAlgorithmsRepository;

    @Autowired
    private IOrderAlgorithmConfigRepository orderAlgorithmConfigRepository;


    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {
        log.info("OrderAlgorithmsDeal 订购处理开始");
        String orderCode = orderGateway.getOrderCode();
        List<String> orderCodeList = Lists.newArrayList();
        orderCodeList.add(orderCode);
        List<OrderAlgorithmsDO> orderAlgorithmsList = orderAlgorithmsRepository.getOrderAlgorithmsByList(orderCodeList, null);
        int moveSign = orderGateway.getOrderMoveSign();
        if (!CollectionUtils.isEmpty(orderAlgorithmsList)) {
            List<OrderAlgorithmsHisDO> orderAlgorithmsHisList = new ArrayList<>();
            for (OrderAlgorithmsDO orderAlgorithmsDO : orderAlgorithmsList) {
                OrderAlgorithmsHisDO hisDO = new OrderAlgorithmsHisDO();
                BeanUtils.copyProperties(orderAlgorithmsDO, hisDO);
                orderAlgorithmsHisList.add(hisDO);
            }
            log.info("OrderAlgorithmsDeal 订购处理开始 1.1");
            orderAlgorithmsHisRepository.saveListBatch(orderAlgorithmsHisList);
            log.info("OrderAlgorithmsDeal 订购处理开始 1.2");
            orderAlgorithmsRepository.deleteByOrderCode(orderCode);
            log.info("OrderAlgorithmsDeal 订购处理开始 1.3");
            this.saveOrderAlgorithmsList(orderGateway, moveSign);
        }
        else {
            this.saveOrderAlgorithmsList(orderGateway, moveSign);
        }

        // 处理算法配置逻辑
        handleAlgorithmConfigLogic(orderGateway);

        log.info("OrderAlgorithmsDeal 订购处理结束");
        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }
    }

    private void handleAlgorithmConfigLogic(OrderGatewayDTO orderGateway) {
        String orderCode = orderGateway.getOrderCode();
        String tenantId = orderGateway.getCustomer().getCustomerCode();

        List<OrderAlgorithmConfigDO> dbList = orderAlgorithmConfigRepository.selectByOrderCode(orderCode);

        List<String> industryCodeList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(orderGateway.getPackages())) {
            for (OrderGatewayDTO.OrderPackages pkg : orderGateway.getPackages()) {
                if (pkg.getApplication() != null && StringUtils.isNotBlank(pkg.getApplication().getAppCode())) {
                    industryCodeList.add(pkg.getApplication().getAppCode());
                }
            }
        }
        if (industryCodeList.isEmpty()) {
            return;
        }

        Set<String> syncKeySet = new HashSet<>();
        List<OrderGatewayDTO.AlgorithmConfigInfo> syncList = orderGateway.getAlgorithmConfigList();
        if (!CollectionUtils.isEmpty(syncList)) {
            for (OrderGatewayDTO.AlgorithmConfigInfo info : syncList) {
                for (String industryCode : industryCodeList) {
                    syncKeySet.add(buildAlgorithmConfigKey(
                            info.getAlgorithmCode(),
                            info.getAlgorithmType(),
                            info.getDeviceCode(),
                            industryCode));
                }
            }
        }

        Set<String> dbKeySet = new HashSet<>();
        for (OrderAlgorithmConfigDO record : dbList) {
            dbKeySet.add(buildAlgorithmConfigKey(
                    record.getAlgorithmCode(),
                    record.getAlgorithmType() != null ? String.valueOf(record.getAlgorithmType()) : "",
                    record.getDeviceCode(),
                    record.getIndustryCode()));
        }

        List<OrderDeviceHisDO> reduceDeviceList = new ArrayList<>();
        List<OrderUavDeviceHisDO> reduceUavDeviceList = new ArrayList<>();
        List<Long> toDeleteIds = new ArrayList<>();
        for (OrderAlgorithmConfigDO record : dbList) {
            String key = buildAlgorithmConfigKey(
                    record.getAlgorithmCode(),
                    record.getAlgorithmType() != null ? String.valueOf(record.getAlgorithmType()) : "",
                    record.getDeviceCode(),
                    record.getIndustryCode());
            if (!syncKeySet.contains(key)) {
                toDeleteIds.add(record.getId());

                OrderDeviceHisDO hisDO = new OrderDeviceHisDO();
                hisDO.setDeviceCode(record.getDeviceCode());
                hisDO.setTenantId(tenantId);
                hisDO.setIndustryCode(record.getIndustryCode());
                hisDO.setOrderCode(orderCode);

                if (record.getAlgorithmType() != null && record.getAlgorithmType() == 1) {
                    reduceDeviceList.add(hisDO);
                } else if (record.getAlgorithmType() != null && record.getAlgorithmType() == 2) {
                    OrderUavDeviceHisDO uavHisDO = new OrderUavDeviceHisDO();
                    uavHisDO.setDeviceCode(record.getDeviceCode());
                    uavHisDO.setTenantId(tenantId);
                    uavHisDO.setIndustryCode(record.getIndustryCode());
                    uavHisDO.setOrderCode(orderCode);
                    reduceUavDeviceList.add(uavHisDO);
                }
            }
        }

        if (!toDeleteIds.isEmpty()) {
            orderAlgorithmConfigRepository.removeByIds(toDeleteIds);
        }
        if (!reduceDeviceList.isEmpty()) {
            if (orderGateway.getReduceAlgorithmConfigDeviceList() == null) {
                orderGateway.setReduceAlgorithmConfigDeviceList(new ArrayList<>());
            }
            orderGateway.getReduceAlgorithmConfigDeviceList().addAll(reduceDeviceList);
        }
        if (!reduceUavDeviceList.isEmpty()) {
            if (orderGateway.getReduceAlgorithmConfigUavDeviceList() == null) {
                orderGateway.setReduceAlgorithmConfigUavDeviceList(new ArrayList<>());
            }
            orderGateway.getReduceAlgorithmConfigUavDeviceList().addAll(reduceUavDeviceList);
        }

        List<OrderAlgorithmConfigDO> toInsert = new ArrayList<>();
        if (!CollectionUtils.isEmpty(syncList)) {
            for (OrderGatewayDTO.AlgorithmConfigInfo info : syncList) {
                for (String industryCode : industryCodeList) {
                    String key = buildAlgorithmConfigKey(
                            info.getAlgorithmCode(),
                            info.getAlgorithmType(),
                            info.getDeviceCode(),
                            industryCode);
                    if (!dbKeySet.contains(key)) {
                        OrderAlgorithmConfigDO newRecord = new OrderAlgorithmConfigDO();
                        newRecord.setOrderCode(orderCode);
                        newRecord.setIndustryCode(industryCode);
                        newRecord.setDeviceCode(info.getDeviceCode());
                        newRecord.setAlgorithmCode(info.getAlgorithmCode());
                        newRecord.setAlgorithmType(info.getAlgorithmType() != null ? Integer.valueOf(info.getAlgorithmType()) : null);
                        newRecord.setTenantId(tenantId);
                        newRecord.setAcceptNode(orderGateway.getAcceptNode());
                        newRecord.setCreator(tenantId);
                        newRecord.setDeleteFlag(0);
                        newRecord.setCreateTime(DateUtils.getNowDate());
                        toInsert.add(newRecord);
                    }
                }
            }
        }
        if (!toInsert.isEmpty()) {
            orderAlgorithmConfigRepository.saveBatch(toInsert);
        }
    }

    private String buildAlgorithmConfigKey(String algorithmCode, String algorithmType, String deviceCode, String industryCode) {
        return (algorithmCode != null ? algorithmCode : "") + "|"
                + (algorithmType != null ? algorithmType : "") + "|"
                + (deviceCode != null ? deviceCode : "") + "|"
                + (industryCode != null ? industryCode : "");
    }

    /**
     * 功能描述 保存新的订单算法关系
     * @author chenmf
     * @date 2022/1/10
     * [orderGateway, moveSign]
     * @return
     */
    private void saveOrderAlgorithmsList(OrderGatewayDTO orderGateway, int moveSign){

        String orderCode = orderGateway.getOrderCode();
        List<OrderGatewayDTO.OrderPackages> packageList = orderGateway.getPackages();
        String customerCode = orderGateway.getCustomer().getCustomerCode();

        if (!CollectionUtils.isEmpty(packageList)) {
            String oldAcceptNode = orderGateway.getOldAcceptNode();
            List<OrderAlgorithmsDO> orderAlgorithmsDOList = new ArrayList<>();
            for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
                String appCode = orderPackages.getApplication().getAppCode();
                String productCode = orderPackages.getApplication().getProductCode();
                List<OrderGatewayDTO.Algorithm> algorithms = orderPackages.getAlgorithms();
                if (!CollectionUtils.isEmpty(algorithms)) {
                    for (OrderGatewayDTO.Algorithm algorithm : algorithms) {

                        OrderAlgorithmsDO orderAlgorithmsDO = new OrderAlgorithmsDO();

                        orderAlgorithmsDO.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL);
                        orderAlgorithmsDO.setAlgorithmCode(algorithm.getAlgorithmCode());
                        orderAlgorithmsDO.setAlgorithmName(algorithm.getAlgorithmName());
                        orderAlgorithmsDO.setTenantId(customerCode);
                        orderAlgorithmsDO.setPackageCode(orderPackages.getPackageCode());
                        orderAlgorithmsDO.setOrderCode(orderCode);
                        orderAlgorithmsDO.setMoveSign(moveSign);
                        orderAlgorithmsDO.setCreateTime(DateUtils.getNowDate());
                        orderAlgorithmsDO.setUpdateTime(DateUtils.getNowDate());
                        orderAlgorithmsDO.setOrdStatus(orderGateway.getStatus());
                        orderAlgorithmsDO.setIndustryCode(appCode);
                        orderAlgorithmsDO.setAppVerCode(productCode);
                        // 如果订单受理节点非当前节点，不更改订单受理节点信息
                        if (BooleanUtils.isTrue(orderGateway.getHandleOrderUserInfoOnly()) && StringUtils.isNotEmpty(oldAcceptNode)) {
                            orderAlgorithmsDO.setAcceptNode(oldAcceptNode);
                        } else {
                            orderAlgorithmsDO.setAcceptNode(orderGateway.getAcceptNode());
                        }
                        orderAlgorithmsDOList.add(orderAlgorithmsDO);
                    }
                }
            }
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(orderAlgorithmsDOList)) {
                orderAlgorithmsRepository.saveListBatch(orderAlgorithmsDOList);
            }
        }
    }
}
