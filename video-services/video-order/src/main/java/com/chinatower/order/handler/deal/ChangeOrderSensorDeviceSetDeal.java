package com.chinatower.order.handler.deal;

import com.alibaba.nacos.common.utils.MapUtils;
import com.chinatower.common.core.domain.OrderSensorDeviceDO;
import com.chinatower.common.core.domain.OrderSensorDeviceHisDO;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.device.entity.dto.IotDeviceMsgDTO;
import com.chinatower.order.domain.OrganDeviceRelDO;
import com.chinatower.order.dto.DeviceIndustryDTO;
import com.chinatower.order.repository.IOrderSensorDeviceHisRepository;
import com.chinatower.order.repository.IOrderSensorDeviceRepository;
import com.chinatower.order.service.IOrderOrganDeviceRelService;
import com.chinatower.order.service.IOrderVideoDeviceService;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.device.DelDeviceRelaDTO;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ChangeOrderSensorDeviceSetDeal
 * @Deacription 变更订单物联传感器设备配置关系处理模块
 * @Author chenmf
 * @Date 2022/1/7 10:58
 * @Version 1.0
 **/
@Component
@Slf4j
public class ChangeOrderSensorDeviceSetDeal extends AbstractOrderDeal {

    @Autowired
    private IOrderSensorDeviceHisRepository orderSensorDeviceHisRepository;

    @Autowired
    private IOrderSensorDeviceRepository orderSensorDeviceRepository;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private IOrderVideoDeviceService orderVideoDeviceService;

    @Autowired
    private IOrderOrganDeviceRelService orderOrganDeviceRelService;


    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {
        log.info("变更订单物联传感器设备配置关系处理模块");
        String orderCode = orderGateway.getOrderCode();
        List<String> orderCodeList = Lists.newArrayList();
        orderCodeList.add(orderCode);
        //查询最新订购的物联传感器设备信息
        List<OrderSensorDeviceDO> orderSensorDeviceList = orderSensorDeviceRepository.getOrderDeviceByList(orderCodeList, null);
        if (!CollectionUtils.isEmpty(orderSensorDeviceList)) {

            //获取迁移次数
            int moveSign = orderSensorDeviceList.get(0).getMoveSign() - 1;
            List<OrderSensorDeviceHisDO> oldOrderDeviceList = orderSensorDeviceHisRepository.getOldOrderDeviceByOrderCode(orderCode, moveSign);

            List<String> orderSensorDeviceKeyList = orderSensorDeviceList.stream()
                    .map(x -> (x.getDeviceCode() + "_" + x.getIndustryCode() + "_" + x.getCategoryCode())).collect(Collectors.toList());
            //需要退订的物联传感器设备
            List<OrderSensorDeviceHisDO> reduceDevList = oldOrderDeviceList.stream().filter(
                    item -> !orderSensorDeviceKeyList.contains(item.getDeviceCode() + "_" + item.getIndustryCode() + "_" + item.getCategoryCode())).collect(Collectors.toList());

            //减少的物联传感器设备处理
            handleDevOrgRela(reduceDevList);

            //新增的物联传感器设备处理
            addSensorDeviceDeal(oldOrderDeviceList, orderGateway);
        } else {
            //获取迁移次数
            int moveSign = orderGateway.getOrderMoveSign() - 1;
            List<OrderSensorDeviceHisDO> reduceDevList = orderSensorDeviceHisRepository.getOldOrderDeviceByOrderCode(orderCode, moveSign);
            if (!CollectionUtils.isEmpty(reduceDevList)) {
                //减少的物联传感器设备处理
                handleDevOrgRela(reduceDevList);
            }
        }

        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }
    }

    /**
     * 新增的物联设备传感器处理
     *
     * @return
     * @author chenmf
     * @date 2022/2/14
     * @version
     */
    private void addSensorDeviceDeal(List<OrderSensorDeviceHisDO> oldOrderDeviceList, OrderGatewayDTO orderGateway) {

        String customerCode = orderGateway.getCustomer().getCustomerCode();
        List<OrderGatewayDTO.OrderPackages> packageList = orderGateway.getPackages();
        if (!CollectionUtils.isEmpty(packageList)) {
            // 设备判重列表，执行过相同设备就不在执行了
            List<String> handleDeviceCodeList = Lists.newArrayList();
            //组装新增物联传感器数据列表信息
            List<IotDeviceMsgDTO> deviceDTOList = new ArrayList<>();
            //组装新增物联传感器和组织关系类列表信息
            List<OrganDeviceRelVO> orgList = new ArrayList<>();
            // 组织判重列表，执行过相同组织就不在执行了
            List<String> handleOrgIdList = Lists.newArrayList();

            Map<String, List<OrderSensorDeviceHisDO>> oldDeviceMap = oldOrderDeviceList.stream()
                    .collect(Collectors.groupingBy(x -> (x.getDeviceCode() + "_" + x.getIndustryCode() + "_" + x.getCategoryCode())));

            // 行业编码列表
            List<String> industryCodeList = packageList.stream().map(x -> x.getApplication().getAppCode()).distinct().collect(Collectors.toList());
            // 行业根节点组织map key -> 行业编码 value -> 根节点组织编码
            Map<String, String> industryDeptMap = getRootDeptMap(customerCode, industryCodeList);

            Set<String> deviceCodeSet = Sets.newHashSet();
            for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
                List<OrderGatewayDTO.SensorDevice> deviceList = orderPackages.getSensorDevice();
                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(deviceList)) {
                    deviceCodeSet.addAll(deviceList.stream().map(OrderGatewayDTO.SensorDevice::getDeviceCode).collect(Collectors.toList()));
                }
            }

            List<DeviceIndustryDTO> existDeviceInfoList
                    = orderOrganDeviceRelService.selectExistDeviceCodeList(customerCode, industryCodeList,
                    Lists.newArrayList(deviceCodeSet), Lists.newArrayList(OrderConstants.CATEGORY_RADAR_CODE, OrderConstants.CATEGORY_SENSOR_CODE));
            List<String> existIndustryDeviceCodeList = Lists.newArrayList();
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(existDeviceInfoList)) {
                existIndustryDeviceCodeList = existDeviceInfoList.stream()
                        .map(x -> assembleOrgIdKey(customerCode, x.getIndustryCode(), x.getDeviceCode(), x.getDeviceType()))
                        .distinct().collect(Collectors.toList());
            }

            Map<String, List<OrderGatewayDTO.SensorDevice>> addDeviceMap = Maps.newHashMap();
            for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
                //过滤传感器类型数据
                List<OrderGatewayDTO.SensorDevice> devList = orderPackages.getSensorDevice();
                String industryCode = orderPackages.getApplication().getAppCode();
                //交集
                List<OrderGatewayDTO.SensorDevice> intersectDev = devList.stream().filter(item ->
                        oldDeviceMap.containsKey(item.getDeviceCode() + "_" + industryCode + "_" + item.getCategoryCode())).collect(Collectors.toList());

                //需要新增的物联设备
                List<OrderGatewayDTO.SensorDevice> addDevList = devList.stream().filter(
                        item -> !intersectDev.stream().map(x -> (x.getDeviceCode() + "_" + x.getCategoryCode())).collect(Collectors.toList()).
                                contains(item.getDeviceCode() + "_" + item.getCategoryCode())).collect(Collectors.toList());

                //获取租户部门列表(顶级结构)
                String deptId = industryDeptMap.get(orderPackages.getApplication().getAppCode());
                if (!CollectionUtils.isEmpty(addDevList)) {
                    addDeviceMap.put(industryCode ,addDevList);

                    for (OrderGatewayDTO.SensorDevice device : addDevList) {
                        if (!handleDeviceCodeList.contains(device.getDeviceCode())) {
                            IotDeviceMsgDTO videoDTO = new IotDeviceMsgDTO();
                            BeanUtils.copyProperties(device, videoDTO);
                            videoDTO.setDeviceName(device.getDevName());
                            videoDTO.setDeviceStatus(device.getStatus());
                            videoDTO.setDeviceType(device.getDeviceType());
                            deviceDTOList.add(videoDTO);
                            handleDeviceCodeList.add(device.getDeviceCode());
                        }

                        int deviceType = 0;
                        if (Objects.equals(OrderConstants.ORDER_RADAR_CODE, device.getCategoryCode())) {
                            deviceType = OrderConstants.CATEGORY_RADAR_CODE;
                        } else if (Objects.equals(OrderConstants.ORDER_SENSOR_CODE, device.getCategoryCode())) {
                            deviceType = OrderConstants.CATEGORY_SENSOR_CODE;
                        }
                        String assembleKey = assembleOrgIdKey(customerCode, industryCode, device.getDeviceCode(), deviceType);

                        if (!StringUtils.isEmpty(deptId)
                                && (!existIndustryDeviceCodeList.contains(assembleKey))
                                && (!handleOrgIdList.contains(assembleKey))) {
                            OrganDeviceRelVO organDeviceRelVO = new OrganDeviceRelVO();
                            organDeviceRelVO.setCreateTime(new Date());
                            organDeviceRelVO.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL_CHAR);
                            organDeviceRelVO.setDeviceCode(device.getDeviceCode());
                            organDeviceRelVO.setUpdateTime(new Date());
                            organDeviceRelVO.setDeptId(deptId);
                            organDeviceRelVO.setTenantId(customerCode);
                            organDeviceRelVO.setDeviceType(deviceType);
                            organDeviceRelVO.setIndustryCode(industryCode);
                            organDeviceRelVO.setAppVerCode(orderPackages.getApplication().getProductCode());
                            orgList.add(organDeviceRelVO);
                            handleOrgIdList.add(assembleKey);
                        }
                    }

                }
            }
            orderGateway.setAddSensorDeviceMap(addDeviceMap);
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(deviceDTOList)) {
                //批量保存和更新物联设备信息
                orderVideoDeviceService.batchSaveOrUpdateIotDevice(deviceDTOList);
            }

            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(orgList)) {
                //批量保存物联设备组织关系
                orderOrganDeviceRelService.batchSaveDeviceRela(orgList);
            }
        }
    }

    private String assembleOrgIdKey(String tenantId, String industryCode, String deviceCode, int deviceType) {
        return String.format("%s_%s_%s_%s", tenantId, industryCode, deviceCode, deviceType);
    }

    private Map<String, String> getRootDeptMap(String customerCode, List<String> industryCodeList) {
        List<SysDept> rootDeptInfoList = orderOrganDeviceRelService.selectTenantRootDeptId(customerCode, industryCodeList);
        Map<String, String> resultMap = Maps.newHashMap();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(rootDeptInfoList)) {
            resultMap = rootDeptInfoList.stream().collect(Collectors.toMap(SysDept::getIndustryCode, SysDept::getDeptId, (a,b) -> a));
        }
        return resultMap;
    }

    /**
     * 功能描述 批量删除组织物联设备关系表
     *
     * @return
     * @author chenmf
     * @date 2022/1/11
     * [reduceDevList]
     */
    private void handleDevOrgRela(List<OrderSensorDeviceHisDO> reduceDevList) {

        if (!CollectionUtils.isEmpty(reduceDevList)) {
            String tenantId = reduceDevList.get(0).getTenantId();
            Map<String, Set<String>> industrySensorDeviceMap = Maps.newHashMap();
            Map<String, Set<String>> industryRadarDeviceMap = Maps.newHashMap();
            for (OrderSensorDeviceHisDO orderSensorDeviceHisDO : reduceDevList) {
                String industryCode = orderSensorDeviceHisDO.getIndustryCode();
                if (Objects.equals(OrderConstants.ORDER_SENSOR_CODE, orderSensorDeviceHisDO.getCategoryCode())) {
                    if (industrySensorDeviceMap.containsKey(industryCode)) {
                        industrySensorDeviceMap.get(industryCode).add(orderSensorDeviceHisDO.getDeviceCode());
                    } else {
                        industrySensorDeviceMap.put(industryCode, Sets.newHashSet(orderSensorDeviceHisDO.getDeviceCode()));
                    }
                } else if (Objects.equals(OrderConstants.ORDER_RADAR_CODE, orderSensorDeviceHisDO.getCategoryCode())) {
                    if (industryRadarDeviceMap.containsKey(industryCode)) {
                        industryRadarDeviceMap.get(industryCode).add(orderSensorDeviceHisDO.getDeviceCode());
                    } else {
                        industryRadarDeviceMap.put(industryCode, Sets.newHashSet(orderSensorDeviceHisDO.getDeviceCode()));
                    }
                }
            }

            // 处理物联设备组织信息
            if (MapUtils.isNotEmpty(industrySensorDeviceMap)) {
                List<String> deviceCodeList = industrySensorDeviceMap.values().stream().flatMap(Set::stream).distinct().collect(Collectors.toList());
                DelDeviceRelaDTO param = new DelDeviceRelaDTO();
                param.setDeviceList(deviceCodeList);
                param.setTenantId(tenantId);
                param.setDeviceType(OrderConstants.CATEGORY_SENSOR_CODE);
                param.setIndustryCodeList(Lists.newArrayList(industrySensorDeviceMap.keySet()));
                List<OrganDeviceRelDO> organDeviceRelList = orderOrganDeviceRelService.selectOrganDeviceInfoByCondition(param);
                if (CollectionUtils.isNotEmpty(organDeviceRelList)) {
                    List<Long> ids = organDeviceRelList.stream()
                            .filter(x -> industrySensorDeviceMap.containsKey(x.getIndustryCode()) && industrySensorDeviceMap.get(x.getIndustryCode()).contains(x.getDeviceCode()))
                            .map(OrganDeviceRelDO::getId).distinct().collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(ids)) {
                        orderOrganDeviceRelService.batchDelDeviceRelaByIds(ids);
                    }
                }
            }

            // 处理雷达设备组织信息
            if (MapUtils.isNotEmpty(industryRadarDeviceMap)) {
                List<String> deviceCodeList = industryRadarDeviceMap.values().stream().flatMap(Set::stream).distinct().collect(Collectors.toList());
                DelDeviceRelaDTO param = new DelDeviceRelaDTO();
                param.setDeviceList(deviceCodeList);
                param.setTenantId(tenantId);
                param.setDeviceType(OrderConstants.CATEGORY_RADAR_CODE);
                param.setIndustryCodeList(Lists.newArrayList(industryRadarDeviceMap.keySet()));
                List<OrganDeviceRelDO> organDeviceRelList = orderOrganDeviceRelService.selectOrganDeviceInfoByCondition(param);
                if (CollectionUtils.isNotEmpty(organDeviceRelList)) {
                    List<Long> ids = organDeviceRelList.stream()
                            .filter(x -> industryRadarDeviceMap.containsKey(x.getIndustryCode()) && industryRadarDeviceMap.get(x.getIndustryCode()).contains(x.getDeviceCode()))
                            .map(OrganDeviceRelDO::getId).distinct().collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(ids)) {
                        orderOrganDeviceRelService.batchDelDeviceRelaByIds(ids);
                    }
                }
            }
        }
    }

}
