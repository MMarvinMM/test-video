package com.chinatower.order.handler.deal;

import com.chinatower.common.core.domain.OrderServiceRelDO;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.core.dto.OrderGatewayDTO.OrderPackages;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.utils.StringUtils;
import com.chinatower.common.core.domain.OrderServiceRelHisDO;
import com.chinatower.device.entity.dto.BasicDeviceDTO;
import com.chinatower.order.repository.IDeviceInfoRepository;
import com.chinatower.order.repository.IOrderServiceRelHisRepository;
import com.chinatower.order.repository.IOrderServiceRelRepository;
import com.chinatower.platform.constants.OrderConstants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName OrderServiceRelDeal
 * @Deacription 订单监控服务关系通用模块
 * @Author liuchao
 * @Date 2022/7/19 10:58
 * @Version 1.0
 **/
@Component
@Slf4j
public class OrderServiceRelDeal extends AbstractOrderDeal {

    @Autowired
    private IOrderServiceRelRepository orderServiceRelRepository;

    @Autowired
    private IOrderServiceRelHisRepository orderServiceRelHisRepository;

    @Autowired
    private IDeviceInfoRepository iDeviceInfoRepository;


    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {
        log.info("OrderServiceRelDeal 订购处理开始");
        String orderCode = orderGateway.getOrderCode();
        List<String> orderCodeList = Lists.newArrayList();
        orderCodeList.add(orderCode);
        List<OrderServiceRelDO> orderServiceRelDOList = orderServiceRelRepository.getOrderServiceRelByList(orderCodeList, null);
        List<OrderServiceRelHisDO> orderServiceRelHisDOList = new ArrayList<>();
        int moveSign = orderGateway.getOrderMoveSign();
        if (!CollectionUtils.isEmpty(orderServiceRelDOList)) {
            for (OrderServiceRelDO orderServiceRelDO : orderServiceRelDOList) {
                OrderServiceRelHisDO hisDO = new OrderServiceRelHisDO();
                BeanUtils.copyProperties(orderServiceRelDO, hisDO);
                orderServiceRelHisDOList.add(hisDO);
            }
            orderServiceRelHisRepository.saveListBatch(orderServiceRelHisDOList);
            orderServiceRelRepository.deleteByOrderCode(orderCode);
            this.saveOrderServicRelList(orderGateway, moveSign);
        } else {
            this.saveOrderServicRelList(orderGateway, moveSign);
        }
        log.info("OrderServiceRelDeal 订购处理结束");
        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }
    }

    /**
     * 功能描述 保存新的订单卫星监控服务关系
     *
     * @return
     * @author chenmf
     * @date 2022/1/10
     * [orderGateway, moveSign]
     */
    private List<OrderServiceRelDO> saveOrderServicRelList(OrderGatewayDTO orderGateway, int moveSign) {
        String orderCode = orderGateway.getOrderCode();
        String customerCode = orderGateway.getCustomer().getCustomerCode();
        List<OrderPackages> packageList = orderGateway.getPackages();
        List<OrderServiceRelDO> orderServiceRelDOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(packageList)) {
            Map<String, BasicDeviceDTO> areaInfoMap = getAreaInfoMap(packageList);
            String oldAcceptNode = orderGateway.getOldAcceptNode();
            for (OrderPackages orderPackages : packageList) {
                List<OrderGatewayDTO.Service> serviceList = orderPackages.getServices();
                String appCode = orderPackages.getApplication().getAppCode();
                String productCode = orderPackages.getApplication().getProductCode();
                if (!CollectionUtils.isEmpty(serviceList)) {
                    for (OrderGatewayDTO.Service service : serviceList) {
                        OrderServiceRelDO orderServiceRelDO = new OrderServiceRelDO();
                        orderServiceRelDO.setCountyCode(service.getAreaCodes());
                        orderServiceRelDO.setPackageCode(orderPackages.getPackageCode());
                        orderServiceRelDO.setServiceCode(service.getServiceCode());
                        orderServiceRelDO.setServiceName(service.getServiceName());
                        orderServiceRelDO.setOrderCode(orderCode);
                        orderServiceRelDO.setTenantId(customerCode);
                        orderServiceRelDO.setMoveSign(moveSign);
                        orderServiceRelDO.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL);
                        orderServiceRelDO.setCreateTime(DateUtils.getNowDate());
                        orderServiceRelDO.setUpdateTime(DateUtils.getNowDate());
                        orderServiceRelDO.setOrdStatus(orderGateway.getStatus());
                        orderServiceRelDO.setIndustryCode(appCode);
                        orderServiceRelDO.setAppVerCode(productCode);
                        // 如果订单受理节点非当前节点，不更改订单受理节点信息
                        if (BooleanUtils.isTrue(orderGateway.getHandleOrderUserInfoOnly()) && StringUtils.isNotEmpty(oldAcceptNode)) {
                            orderServiceRelDO.setAcceptNode(oldAcceptNode);
                        } else {
                            orderServiceRelDO.setAcceptNode(orderGateway.getAcceptNode());
                        }
                        if (OrderConstants.CATEGORY_SATELLITE_SERVICE_CODE.equals(service.getServiceCode())
                                && !StringUtils.isEmpty(service.getAreaCodes())) {
                            for (String areaCode : service.getAreaCodes().split(",")) {
                                OrderServiceRelDO orderServiceRelDONew = new OrderServiceRelDO();
                                BeanUtils.copyProperties(orderServiceRelDO, orderServiceRelDONew);
                                orderServiceRelDONew.setCountyCode(areaCode);
                                BasicDeviceDTO basicDeviceDTO = areaInfoMap.get(areaCode);
                                if (basicDeviceDTO != null) {
                                    orderServiceRelDONew.setProviceCode(basicDeviceDTO.getProvinceId());
                                    orderServiceRelDONew.setCityCode(basicDeviceDTO.getCityId());
                                }
                                orderServiceRelDOList.add(orderServiceRelDONew);
                            }
                        } else {
                            orderServiceRelDOList.add(orderServiceRelDO);
                        }
                    }
                }
            }
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(orderServiceRelDOList)) {
                orderServiceRelRepository.saveListBatch(orderServiceRelDOList);
            }
        }
        return orderServiceRelDOList;
    }

    private Map<String, BasicDeviceDTO> getAreaInfoMap(List<OrderPackages> packageList) {
        List<String> areaCodeList = Lists.newArrayList();
        for (OrderPackages orderPackages : packageList) {
            List<OrderGatewayDTO.Service> serviceList = orderPackages.getServices();
            if (!CollectionUtils.isEmpty(serviceList)) {
                for (OrderGatewayDTO.Service service : serviceList) {
                    if (OrderConstants.CATEGORY_SATELLITE_SERVICE_CODE.equals(service.getServiceCode())
                            && !StringUtils.isEmpty(service.getAreaCodes())) {
                        for (String areaCode : service.getAreaCodes().split(",")) {
                            if (!StringUtils.isEmpty(areaCode) && !areaCodeList.contains(areaCode)) {
                                areaCodeList.add(areaCode);
                            }
                        }
                    }
                }
            }
        }
        Map<String, BasicDeviceDTO> resultMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(areaCodeList)) {
            List<BasicDeviceDTO> deviceInfoList = iDeviceInfoRepository.queryOrderDeviceAreaInfoList(areaCodeList);
            if (CollectionUtils.isNotEmpty(deviceInfoList)) {
                resultMap = deviceInfoList.stream().collect(Collectors.toMap(BasicDeviceDTO::getCountyId, (p) -> p, (a,b) -> a));
            }
        }
        return resultMap;
    }
}
