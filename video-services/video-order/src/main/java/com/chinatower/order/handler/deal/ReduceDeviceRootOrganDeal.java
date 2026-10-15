package com.chinatower.order.handler.deal;

import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.order.domain.OrganDeviceRelDO;
import com.chinatower.order.service.IOrderOrganDeviceRelService;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.platform.entity.device.DelDeviceRelaDTO;
import com.chinatower.platform.entity.order.OrganStreetRelVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName DeviceRootOrganShip
 * @Deacription 退订处理组织与设备关系；
 * @Author chenmf
 * @Date 2022/1/7 10:58
 * @Version 1.0
 **/
@Component
public class ReduceDeviceRootOrganDeal extends AbstractOrderDeal {

    @Autowired
    private IOrderOrganDeviceRelService orderOrganDeviceRelService;

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {

        String customerCode = orderGateway.getCustomer().getCustomerCode();
        List<OrderGatewayDTO.OrderPackages> packageList = orderGateway.getPackages();

        if (!CollectionUtils.isEmpty(packageList)) {
            //批量删除退订设备与组织关系
            batchDelDeviceRela(customerCode, packageList);

            //批量删除退订物联设备与组织关系
            batchDelIotDeviceRela(customerCode, packageList);

            //批量删除退订卫星服务与组织关系
            batchDelServiceRelRela(customerCode, packageList);
            //更新组织行政区域表（先删后增）
            Map<String, String> industryMap = packageList.stream()
                    .collect(Collectors.toMap(x -> x.getApplication().getAppCode(), x -> x.getApplication().getProductCode(), (a, b) -> a));

            orderOrganDeviceRelService.updateDeptStreetRelBatch(customerCode, industryMap);
        }

        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }
    }

    /**
     * 批量删除退订设置与组织关系
     *
     * @return
     * @author chenmf
     * @date 2022/5/26
     * @version
     */
    private void batchDelDeviceRela(String customerCode, List<OrderGatewayDTO.OrderPackages> packageList) {

        Map<String, Set<String>> industryDeviceMap = Maps.newHashMap();
        for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
            String industryCode = orderPackages.getApplication().getAppCode();
            List<OrderGatewayDTO.Device> deviceList = orderPackages.getDevices();
            if (!CollectionUtils.isEmpty(deviceList)) {

                Set<String> reduceDevSet = deviceList.stream().map(OrderGatewayDTO.Device::getDeviceCode)
                        .collect(Collectors.toSet());

                if (industryDeviceMap.containsKey(industryCode)) {
                    industryDeviceMap.get(industryCode).addAll(reduceDevSet);
                } else {
                    industryDeviceMap.put(industryCode, reduceDevSet);
                }
            }
        }

        List<String> deviceCodeList = industryDeviceMap.values().stream().flatMap(Set::stream).distinct().collect(Collectors.toList());

        DelDeviceRelaDTO param = new DelDeviceRelaDTO();
        param.setDeviceList(deviceCodeList);
        param.setTenantId(customerCode);
        param.setDeviceType(OrderConstants.CATEGORY_DEVICE_CODE);
        param.setIndustryCodeList(Lists.newArrayList(industryDeviceMap.keySet()));
        List<OrganDeviceRelDO> organDeviceRelList = orderOrganDeviceRelService.selectOrganDeviceInfoByCondition(param);
        if (CollectionUtils.isNotEmpty(organDeviceRelList)) {
            List<Long> ids = organDeviceRelList.stream()
                    .filter(x -> industryDeviceMap.containsKey(x.getIndustryCode()) && industryDeviceMap.get(x.getIndustryCode()).contains(x.getDeviceCode()))
                    .map(OrganDeviceRelDO::getId).distinct().collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(ids)) {
                orderOrganDeviceRelService.batchDelDeviceRelaByIds(ids);
            }
        }
    }

    /**
     * 批量删除退订物联设置与组织关系
     *
     * @return
     * @author chenmf
     * @date 2022/5/26
     * @version
     */
    private void batchDelIotDeviceRela(String customerCode, List<OrderGatewayDTO.OrderPackages> packageList) {

        // 行业物联设备map key -> 行业编码 values -> 物联设备编码列表
        Map<String, Set<String>> industrySensorDeviceMap = Maps.newHashMap();
        // 行业雷达设备map key -> 行业编码 values -> 雷达设备编码列表
        Map<String, Set<String>> industryRadarDeviceMap = Maps.newHashMap();
        for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
            List<OrderGatewayDTO.SensorDevice> deviceList = orderPackages.getSensorDevice();
            String industryCode = orderPackages.getApplication().getAppCode();
            if (!CollectionUtils.isEmpty(deviceList)) {
                //过滤物联传感器设备列表信息
                Set<String> reduceSensorDevSet = deviceList.stream().filter(u ->
                        Objects.equals(OrderConstants.ORDER_SENSOR_CODE, u.getCategoryCode()))
                        .map(OrderGatewayDTO.SensorDevice::getDeviceCode).collect(Collectors.toSet());
                if (CollectionUtils.isNotEmpty(reduceSensorDevSet)) {
                    if (industrySensorDeviceMap.containsKey(industryCode)) {
                        industrySensorDeviceMap.get(industryCode).addAll(reduceSensorDevSet);
                    } else {
                        industrySensorDeviceMap.put(industryCode, reduceSensorDevSet);
                    }
                }

                //过滤雷达传感器设备列表信息
                Set<String> reduceRadarDevList = deviceList.stream().filter(u ->
                        Objects.equals(OrderConstants.ORDER_RADAR_CODE, u.getCategoryCode()))
                        .map(OrderGatewayDTO.SensorDevice::getDeviceCode).collect(Collectors.toSet());
                if (CollectionUtils.isNotEmpty(reduceRadarDevList)) {

                    if (industryRadarDeviceMap.containsKey(industryCode)) {
                        industryRadarDeviceMap.get(industryCode).addAll(reduceRadarDevList);
                    } else {
                        industryRadarDeviceMap.put(industryCode, reduceRadarDevList);
                    }
                }
            }
        }

        // 处理物联设备需要删除的设备组织关联信息
        if (industrySensorDeviceMap.size() > 0) {
            List<String> sensorDeviceCodeList = industrySensorDeviceMap.values().stream().flatMap(Set::stream).distinct().collect(Collectors.toList());
            DelDeviceRelaDTO sensorParam = new DelDeviceRelaDTO();
            sensorParam.setDeviceList(sensorDeviceCodeList);
            sensorParam.setTenantId(customerCode);
            sensorParam.setDeviceType(OrderConstants.CATEGORY_SENSOR_CODE);
            sensorParam.setIndustryCodeList(Lists.newArrayList(industrySensorDeviceMap.keySet()));
            List<OrganDeviceRelDO> sensorOrganDeviceRelList = orderOrganDeviceRelService.selectOrganDeviceInfoByCondition(sensorParam);
            if (CollectionUtils.isNotEmpty(sensorOrganDeviceRelList)) {
                List<Long> ids = sensorOrganDeviceRelList.stream()
                        .filter(x -> industrySensorDeviceMap.containsKey(x.getIndustryCode()) && industrySensorDeviceMap.get(x.getIndustryCode()).contains(x.getDeviceCode()))
                        .map(OrganDeviceRelDO::getId).distinct().collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(ids)) {
                    orderOrganDeviceRelService.batchDelDeviceRelaByIds(ids);
                }
            }
        }

        // 处理雷达设备需要删除的设备组织关联信息
        if (industryRadarDeviceMap.size() > 0) {
            List<String> radarDeviceCodeList = industryRadarDeviceMap.values().stream().flatMap(Set::stream).distinct().collect(Collectors.toList());
            DelDeviceRelaDTO radarParam = new DelDeviceRelaDTO();
            radarParam.setDeviceList(radarDeviceCodeList);
            radarParam.setTenantId(customerCode);
            radarParam.setDeviceType(OrderConstants.CATEGORY_RADAR_CODE);
            radarParam.setIndustryCodeList(Lists.newArrayList(industryRadarDeviceMap.keySet()));
            List<OrganDeviceRelDO> radarOrganDeviceRelList = orderOrganDeviceRelService.selectOrganDeviceInfoByCondition(radarParam);
            if (CollectionUtils.isNotEmpty(radarOrganDeviceRelList)) {
                List<Long> ids = radarOrganDeviceRelList.stream()
                        .filter(x -> industryRadarDeviceMap.containsKey(x.getIndustryCode()) && industryRadarDeviceMap.get(x.getIndustryCode()).contains(x.getDeviceCode()))
                        .map(OrganDeviceRelDO::getId).distinct().collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(ids)) {
                    orderOrganDeviceRelService.batchDelDeviceRelaByIds(ids);
                }
            }
        }
    }

    private void batchDelServiceRelRela(String customerCode, List<OrderGatewayDTO.OrderPackages> packageList) {
        if (!CollectionUtils.isEmpty(packageList)) {
            // 卫星服务map key -> 行业编码 values -> 区域编码列表
            Map<String, Set<String>> industryServiceMap = Maps.newHashMap();

            for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
                //过滤传感器类型数据
                if (CollectionUtils.isEmpty(orderPackages.getServices())) {
                    continue;
                }
                String industryCode = orderPackages.getApplication().getAppCode();
                Set<String> areaCodeSet = Sets.newHashSet();
                orderPackages.getServices().forEach( dev -> {
                    if (!StringUtils.isEmpty(dev.getAreaCodes())) {
                        Collections.addAll(areaCodeSet, dev.getAreaCodes().split(","));
                    }
                });
                if (!CollectionUtils.isEmpty(areaCodeSet)) {
                    if (industryServiceMap.containsKey(industryCode)) {
                        industryServiceMap.get(industryCode).addAll(areaCodeSet);
                    } else {
                        industryServiceMap.put(industryCode, areaCodeSet);
                    }
                }
            }

            // 处理卫星服务需要删除的服务组织关联信息
            if (industryServiceMap.size() > 0) {
                List<String> areaCodeList = industryServiceMap.values().stream().flatMap(Set::stream).distinct().collect(Collectors.toList());
                List<OrganStreetRelVO> organStreetRelList = orderOrganDeviceRelService.selectOrganStreetInfoByCondition(customerCode, OrderConstants.CATEGORY_SATELLITE_CODE,
                        Lists.newArrayList(industryServiceMap.keySet()), areaCodeList);
                if (CollectionUtils.isNotEmpty(organStreetRelList)) {
                    List<Long> ids = organStreetRelList.stream()
                            .filter(x -> industryServiceMap.containsKey(x.getIndustryCode()) && industryServiceMap.get(x.getIndustryCode()).contains(x.getCountyCode()))
                            .map(OrganStreetRelVO::getId).distinct().collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(ids)) {
                        orderOrganDeviceRelService.batchDelStreetRelaByIds(ids);
                    }
                }
            }
        }

    }
}
