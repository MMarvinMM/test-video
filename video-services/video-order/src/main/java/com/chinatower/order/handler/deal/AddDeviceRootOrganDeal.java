package com.chinatower.order.handler.deal;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.core.dto.OrderGatewayDTO.OrderPackages;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.utils.StringUtils;
import com.chinatower.order.dto.DeviceIndustryDTO;
import com.chinatower.order.service.IOrderOrganDeviceRelService;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import com.chinatower.platform.entity.order.OrganStreetRelVO;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.model.SysAreaVO;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName DeviceRootOrganShip
 * @Deacription 组织管理优化，默认创建根结点，根结点赋予全部设备权限
 * @Author chenmf
 * @Date 2022/1/7 10:58
 * @Version 1.0
 **/
@Component
@Slf4j
public class AddDeviceRootOrganDeal extends AbstractOrderDeal {

    @Autowired
    private IOrderOrganDeviceRelService orderOrganDeviceRelService;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {

        OrderGatewayDTO.Customer customer = orderGateway.getCustomer();
        String customerCode = customer.getCustomerCode();
        List<OrderGatewayDTO.OrderPackages> packageList = orderGateway.getPackages();

        // 行业编码列表
        List<String> industryCodeList = packageList.stream().map(x -> x.getApplication().getAppCode()).distinct().collect(Collectors.toList());
        // 行业根节点组织map key -> 行业编码 value -> 根节点组织编码
        Map<String, String> industryDeptMap = getRootDeptMap(customerCode, industryCodeList);

        //新增设备挂组织处理
        saveDeviceRela(customerCode, packageList, industryDeptMap);
        //新增物联设备、雷达挂组织处理
        saveIotDeviceRela(customerCode, packageList, industryDeptMap);
        //新增无人机设备挂组织处理
        saveUavDeviceRela(customerCode, packageList, industryDeptMap);
        //新增无人机机巢设备挂组织处理
        saveUavNestDeviceRela(customerCode, packageList, industryDeptMap);
        //新增喇叭设备挂组织处理
        saveHornDeviceRela(customerCode, packageList, industryDeptMap);
        //新增执法记录仪设备挂组织处理
        saveRecorderDeviceRela(customerCode, packageList, industryDeptMap);
        //新增卫星数据挂组织处理 vm_organ_street_rel_t 表
        addOrganStreet(customerCode, packageList, industryDeptMap);
        //更新组织行政区域表（先删后增）
        Map<String, String> industryMap = packageList.stream()
                .collect(Collectors.toMap(x -> x.getApplication().getAppCode(), x -> x.getApplication().getProductCode(), (a, b) -> a));

        orderOrganDeviceRelService.updateDeptStreetRelBatch(customerCode, industryMap);

        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }
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
     * 新增设备挂组织处理
     *
     * @return
     * @author chenmf
     * @date 2022/5/26
     * @version
     */
    private void saveDeviceRela(String customerCode, List<OrderGatewayDTO.OrderPackages> packageList, Map<String, String> industryDeptMap) {
        List<String> handleOrgIdList = Lists.newArrayList();
        List<OrganDeviceRelVO> orgList = new ArrayList<>();

        Set<String> deviceCodeSet = Sets.newHashSet();
        Set<String> industryCodeSet = Sets.newHashSet();
        for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
            List<OrderGatewayDTO.Device> deviceList = orderPackages.getDevices();
            if (CollectionUtils.isNotEmpty(deviceList)) {
                deviceCodeSet.addAll(deviceList.stream().map(OrderGatewayDTO.Device::getDeviceCode).collect(Collectors.toList()));
            }
            industryCodeSet.add(orderPackages.getApplication().getAppCode());
        }

        List<DeviceIndustryDTO> existDeviceInfoList
                = orderOrganDeviceRelService.selectExistDeviceCodeList(customerCode, Lists.newArrayList(industryCodeSet),
                Lists.newArrayList(deviceCodeSet), Lists.newArrayList(OrderConstants.CATEGORY_DEVICE_CODE));
        List<String> existIndustryDeviceCodeList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(existDeviceInfoList)) {
            existIndustryDeviceCodeList = existDeviceInfoList.stream()
                    .map(x -> assembleOrgIdKey(customerCode, x.getIndustryCode(), x.getDeviceCode(), x.getDeviceType()))
                    .distinct().collect(Collectors.toList());
        }

        for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
            List<OrderGatewayDTO.Device> deviceList = orderPackages.getDevices();
            String industryCode = orderPackages.getApplication().getAppCode();
            String deptId = industryDeptMap.get(industryCode);
            if (!CollectionUtils.isEmpty(deviceList) && StringUtils.isNotEmpty(deptId)) {
                for (OrderGatewayDTO.Device device : deviceList) {
                    String assembleKey = assembleOrgIdKey(customerCode, industryCode, device.getDeviceCode(),
                            OrderConstants.CATEGORY_DEVICE_CODE);
                    if (existIndustryDeviceCodeList.contains(assembleKey)
                            || handleOrgIdList.contains(assembleKey)) {
                        continue;
                    }
                    OrganDeviceRelVO organDeviceRelVO = new OrganDeviceRelVO();
                    organDeviceRelVO.setCreateTime(new Date());
                    organDeviceRelVO.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL_CHAR);
                    organDeviceRelVO.setDeviceCode(device.getDeviceCode());
                    organDeviceRelVO.setUpdateTime(new Date());
                    organDeviceRelVO.setDeviceType(OrderConstants.CATEGORY_DEVICE_CODE);
                    organDeviceRelVO.setDeptId(deptId);
                    organDeviceRelVO.setTenantId(customerCode);
                    organDeviceRelVO.setIndustryCode(industryCode);
                    organDeviceRelVO.setAppVerCode(orderPackages.getApplication().getProductCode());

                    orgList.add(organDeviceRelVO);
                    handleOrgIdList.add(assembleKey);
                }
            }
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(orgList)) {
            orderOrganDeviceRelService.batchSaveDeviceRela(orgList);
        }
    }

    private String assembleOrgIdKey(String tenantId, String industryCode, String deviceCode, int deviceType) {
        return String.format("%s_%s_%s_%s", tenantId, industryCode, deviceCode, deviceType);
    }

    /**
     * 新增物联设备挂组织处理
     *
     * @return
     * @author chenmf
     * @date 2022/5/26
     * @version
     */
    private void saveIotDeviceRela(String customerCode, List<OrderGatewayDTO.OrderPackages> packageList, Map<String, String> industryDeptMap) {

        //组装物联设备组织关系列表信息
        List<OrganDeviceRelVO> orgList = new ArrayList<>();
        List<String> handleOrgIdList = Lists.newArrayList();

        Set<String> deviceCodeSet = Sets.newHashSet();
        Set<String> industryCodeSet = Sets.newHashSet();
        for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
            List<OrderGatewayDTO.SensorDevice> deviceList = orderPackages.getSensorDevice();
            if (CollectionUtils.isNotEmpty(deviceList)) {
                deviceCodeSet.addAll(deviceList.stream().map(OrderGatewayDTO.SensorDevice::getDeviceCode).collect(Collectors.toList()));
            }
            industryCodeSet.add(orderPackages.getApplication().getAppCode());
        }

        List<DeviceIndustryDTO> existDeviceInfoList
                = orderOrganDeviceRelService.selectExistDeviceCodeList(customerCode, Lists.newArrayList(industryCodeSet),
                Lists.newArrayList(deviceCodeSet), Lists.newArrayList(OrderConstants.CATEGORY_RADAR_CODE, OrderConstants.CATEGORY_SENSOR_CODE));
        List<String> existIndustryDeviceCodeList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(existDeviceInfoList)) {
            existIndustryDeviceCodeList = existDeviceInfoList.stream()
                    .map(x -> assembleOrgIdKey(customerCode, x.getIndustryCode(), x.getDeviceCode(), x.getDeviceType()))
                    .distinct().collect(Collectors.toList());
        }

        for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
            List<OrderGatewayDTO.SensorDevice> deviceList = orderPackages.getSensorDevice();
            String deptId = industryDeptMap.get(orderPackages.getApplication().getAppCode());
            String industryCode = orderPackages.getApplication().getAppCode();
            if (!CollectionUtils.isEmpty(deviceList) && StringUtils.isNotEmpty(deptId)) {
                for (OrderGatewayDTO.SensorDevice device : deviceList) {
                    int deviceType = getDeviceType(device.getCategoryCode());
                    String assembleKey = assembleOrgIdKey(customerCode, industryCode, device.getDeviceCode(), deviceType);
                    if (handleOrgIdList.contains(assembleKey)) {
                        continue;
                    }

                    if (existIndustryDeviceCodeList.contains(assembleKey)) {
                        continue;
                    }
                    OrganDeviceRelVO organDeviceRelVO = new OrganDeviceRelVO();
                    organDeviceRelVO.setCreateTime(new Date());
                    organDeviceRelVO.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL_CHAR);
                    organDeviceRelVO.setDeviceCode(device.getDeviceCode());
                    organDeviceRelVO.setUpdateTime(new Date());
                    organDeviceRelVO.setDeptId(deptId);
                    organDeviceRelVO.setDeviceType(deviceType);
                    organDeviceRelVO.setTenantId(customerCode);
                    organDeviceRelVO.setIndustryCode(industryCode);
                    organDeviceRelVO.setAppVerCode(orderPackages.getApplication().getProductCode());

                    orgList.add(organDeviceRelVO);
                    handleOrgIdList.add(assembleKey);
                }
            }
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(orgList)) {
            //批量保存设备组织关系
            orderOrganDeviceRelService.batchSaveDeviceRela(orgList);
        }
    }

    private void saveHornDeviceRela(String customerCode, List<OrderGatewayDTO.OrderPackages> packageList, Map<String, String> industryDeptMap) {

        //组装物联设备组织关系列表信息
        List<OrganDeviceRelVO> orgList = new ArrayList<>();
        List<String> handleOrgIdList = Lists.newArrayList();

        Set<String> deviceCodeSet = Sets.newHashSet();
        Set<String> industryCodeSet = Sets.newHashSet();
        for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
            List<OrderGatewayDTO.SensorDevice> deviceList = orderPackages.getHornDevice();
            if (CollectionUtils.isNotEmpty(deviceList)) {
                deviceCodeSet.addAll(deviceList.stream().map(OrderGatewayDTO.SensorDevice::getDeviceCode).collect(Collectors.toList()));
            }
            industryCodeSet.add(orderPackages.getApplication().getAppCode());
        }

        List<DeviceIndustryDTO> existDeviceInfoList
                = orderOrganDeviceRelService.selectExistDeviceCodeList(customerCode, Lists.newArrayList(industryCodeSet),
                Lists.newArrayList(deviceCodeSet), Lists.newArrayList(OrderConstants.CATEGORY_HORN_CODE));
        List<String> existIndustryDeviceCodeList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(existDeviceInfoList)) {
            existIndustryDeviceCodeList = existDeviceInfoList.stream()
                    .map(x -> assembleOrgIdKey(customerCode, x.getIndustryCode(), x.getDeviceCode(), x.getDeviceType()))
                    .distinct().collect(Collectors.toList());
        }

        int deviceType = OrderConstants.CATEGORY_HORN_CODE;
        for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
            List<OrderGatewayDTO.SensorDevice> deviceList = orderPackages.getHornDevice();
            String deptId = industryDeptMap.get(orderPackages.getApplication().getAppCode());
            String industryCode = orderPackages.getApplication().getAppCode();
            if (!CollectionUtils.isEmpty(deviceList) && StringUtils.isNotEmpty(deptId)) {
                for (OrderGatewayDTO.SensorDevice device : deviceList) {
                    String assembleKey = assembleOrgIdKey(customerCode, industryCode, device.getDeviceCode(), deviceType);
                    if (handleOrgIdList.contains(assembleKey)) {
                        continue;
                    }
                    if (existIndustryDeviceCodeList.contains(assembleKey)) {
                        continue;
                    }
                    OrganDeviceRelVO organDeviceRelVO = new OrganDeviceRelVO();
                    organDeviceRelVO.setCreateTime(new Date());
                    organDeviceRelVO.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL_CHAR);
                    organDeviceRelVO.setDeviceCode(device.getDeviceCode());
                    organDeviceRelVO.setUpdateTime(new Date());
                    organDeviceRelVO.setDeptId(deptId);
                    organDeviceRelVO.setDeviceType(deviceType);
                    organDeviceRelVO.setTenantId(customerCode);
                    organDeviceRelVO.setIndustryCode(industryCode);
                    organDeviceRelVO.setAppVerCode(orderPackages.getApplication().getProductCode());

                    orgList.add(organDeviceRelVO);
                    handleOrgIdList.add(assembleKey);
                }
            }
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(orgList)) {
            //批量保存设备组织关系
            orderOrganDeviceRelService.batchSaveDeviceRela(orgList);
        }
    }

    private void saveUavDeviceRela(String customerCode, List<OrderGatewayDTO.OrderPackages> packageList, Map<String, String> industryDeptMap) {
        List<String> handleOrgIdList = Lists.newArrayList();
        List<OrganDeviceRelVO> orgList = new ArrayList<>();

        Set<String> deviceCodeSet = Sets.newHashSet();
        Set<String> industryCodeSet = Sets.newHashSet();
        for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
            List<OrderGatewayDTO.Device> deviceList = orderPackages.getUavDevice();
            if (CollectionUtils.isNotEmpty(deviceList)) {
                deviceCodeSet.addAll(deviceList.stream().map(OrderGatewayDTO.Device::getDeviceCode).collect(Collectors.toList()));
            }
            industryCodeSet.add(orderPackages.getApplication().getAppCode());
        }

        List<DeviceIndustryDTO> existDeviceInfoList
                = orderOrganDeviceRelService.selectExistDeviceCodeList(customerCode, Lists.newArrayList(industryCodeSet),
                Lists.newArrayList(deviceCodeSet), Lists.newArrayList(OrderConstants.CATEGORY_UAV_CODE));
        List<String> existIndustryDeviceCodeList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(existDeviceInfoList)) {
            existIndustryDeviceCodeList = existDeviceInfoList.stream()
                    .map(x -> assembleOrgIdKey(customerCode, x.getIndustryCode(), x.getDeviceCode(), x.getDeviceType()))
                    .distinct().collect(Collectors.toList());
        }
        for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
            List<OrderGatewayDTO.Device> deviceList = orderPackages.getUavDevice();
            String industryCode = orderPackages.getApplication().getAppCode();
            String deptId = industryDeptMap.get(industryCode);
            if (!CollectionUtils.isEmpty(deviceList) && StringUtils.isNotEmpty(deptId)) {
                for (OrderGatewayDTO.Device device : deviceList) {
                    String assembleKey = assembleOrgIdKey(customerCode, industryCode, device.getDeviceCode(),
                            OrderConstants.CATEGORY_UAV_CODE);
                    if (existIndustryDeviceCodeList.contains(assembleKey)
                            || handleOrgIdList.contains(assembleKey)) {
                        continue;
                    }
                    OrganDeviceRelVO organDeviceRelVO = new OrganDeviceRelVO();
                    organDeviceRelVO.setCreateTime(new Date());
                    organDeviceRelVO.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL_CHAR);
                    organDeviceRelVO.setDeviceCode(device.getDeviceCode());
                    organDeviceRelVO.setUpdateTime(new Date());
                    organDeviceRelVO.setDeviceType(OrderConstants.CATEGORY_UAV_CODE);
                    organDeviceRelVO.setDeptId(deptId);
                    organDeviceRelVO.setTenantId(customerCode);
                    organDeviceRelVO.setIndustryCode(industryCode);
                    organDeviceRelVO.setAppVerCode(orderPackages.getApplication().getProductCode());

                    orgList.add(organDeviceRelVO);
                    handleOrgIdList.add(assembleKey);
                }
            }
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(orgList)) {
            orderOrganDeviceRelService.batchSaveDeviceRela(orgList);
        }
    }

    private void saveUavNestDeviceRela(String customerCode, List<OrderGatewayDTO.OrderPackages> packageList, Map<String, String> industryDeptMap) {
        List<String> handleOrgIdList = Lists.newArrayList();
        List<OrganDeviceRelVO> orgList = new ArrayList<>();

        Set<String> deviceCodeSet = Sets.newHashSet();
        Set<String> industryCodeSet = Sets.newHashSet();
        for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
            List<OrderGatewayDTO.Device> deviceList = orderPackages.getNestDevice();
            if (CollectionUtils.isNotEmpty(deviceList)) {
                deviceCodeSet.addAll(deviceList.stream().map(OrderGatewayDTO.Device::getDeviceCode).collect(Collectors.toList()));
            }
            industryCodeSet.add(orderPackages.getApplication().getAppCode());
        }

        List<DeviceIndustryDTO> existDeviceInfoList
                = orderOrganDeviceRelService.selectExistDeviceCodeList(customerCode, Lists.newArrayList(industryCodeSet),
                Lists.newArrayList(deviceCodeSet), Lists.newArrayList(OrderConstants.CATEGORY_UAV_NEST_CODE));
        List<String> existIndustryDeviceCodeList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(existDeviceInfoList)) {
            existIndustryDeviceCodeList = existDeviceInfoList.stream()
                    .map(x -> assembleOrgIdKey(customerCode, x.getIndustryCode(), x.getDeviceCode(), x.getDeviceType()))
                    .distinct().collect(Collectors.toList());
        }
        for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
            List<OrderGatewayDTO.Device> deviceList = orderPackages.getNestDevice();
            String industryCode = orderPackages.getApplication().getAppCode();
            String deptId = industryDeptMap.get(industryCode);
            if (!CollectionUtils.isEmpty(deviceList) && StringUtils.isNotEmpty(deptId)) {
                for (OrderGatewayDTO.Device device : deviceList) {
                    String assembleKey = assembleOrgIdKey(customerCode, industryCode, device.getDeviceCode(),
                            OrderConstants.CATEGORY_UAV_NEST_CODE);
                    if (existIndustryDeviceCodeList.contains(assembleKey)
                            || handleOrgIdList.contains(assembleKey)) {
                        continue;
                    }
                    OrganDeviceRelVO organDeviceRelVO = new OrganDeviceRelVO();
                    organDeviceRelVO.setCreateTime(new Date());
                    organDeviceRelVO.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL_CHAR);
                    organDeviceRelVO.setDeviceCode(device.getDeviceCode());
                    organDeviceRelVO.setUpdateTime(new Date());
                    organDeviceRelVO.setDeviceType(OrderConstants.CATEGORY_UAV_NEST_CODE);
                    organDeviceRelVO.setDeptId(deptId);
                    organDeviceRelVO.setTenantId(customerCode);
                    organDeviceRelVO.setIndustryCode(industryCode);
                    organDeviceRelVO.setAppVerCode(orderPackages.getApplication().getProductCode());

                    orgList.add(organDeviceRelVO);
                    handleOrgIdList.add(assembleKey);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(orgList)) {
            orderOrganDeviceRelService.batchSaveDeviceRela(orgList);
        }
    }

    private void saveRecorderDeviceRela(String customerCode, List<OrderGatewayDTO.OrderPackages> packageList, Map<String, String> industryDeptMap) {
        List<String> handleOrgIdList = Lists.newArrayList();
        List<OrganDeviceRelVO> orgList = new ArrayList<>();

        Set<String> deviceCodeSet = Sets.newHashSet();
        Set<String> industryCodeSet = Sets.newHashSet();
        for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
            List<OrderGatewayDTO.Device> deviceList = orderPackages.getRecorderDevice();
            if (CollectionUtils.isNotEmpty(deviceList)) {
                deviceCodeSet.addAll(deviceList.stream().map(OrderGatewayDTO.Device::getDeviceCode).collect(Collectors.toList()));
            }
            industryCodeSet.add(orderPackages.getApplication().getAppCode());
        }

        List<DeviceIndustryDTO> existDeviceInfoList
                = orderOrganDeviceRelService.selectExistDeviceCodeList(customerCode, Lists.newArrayList(industryCodeSet),
                Lists.newArrayList(deviceCodeSet), Lists.newArrayList(OrderConstants.CATEGORY_HORN_CODE));
        List<String> existIndustryDeviceCodeList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(existDeviceInfoList)) {
            existIndustryDeviceCodeList = existDeviceInfoList.stream()
                    .map(x -> assembleOrgIdKey(customerCode, x.getIndustryCode(), x.getDeviceCode(), x.getDeviceType()))
                    .distinct().collect(Collectors.toList());
        }
        for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
            List<OrderGatewayDTO.Device> deviceList = orderPackages.getRecorderDevice();
            String industryCode = orderPackages.getApplication().getAppCode();
            String deptId = industryDeptMap.get(industryCode);
            if (!CollectionUtils.isEmpty(deviceList) && StringUtils.isNotEmpty(deptId)) {
                for (OrderGatewayDTO.Device device : deviceList) {
                    String assembleKey = assembleOrgIdKey(customerCode, industryCode, device.getDeviceCode(),
                            OrderConstants.CATEGORY_RECORDER_CODE);
                    if (existIndustryDeviceCodeList.contains(assembleKey)
                            || handleOrgIdList.contains(assembleKey)) {
                        continue;
                    }
                    OrganDeviceRelVO organDeviceRelVO = new OrganDeviceRelVO();
                    organDeviceRelVO.setCreateTime(new Date());
                    organDeviceRelVO.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL_CHAR);
                    organDeviceRelVO.setDeviceCode(device.getDeviceCode());
                    organDeviceRelVO.setUpdateTime(new Date());
                    organDeviceRelVO.setDeviceType(OrderConstants.CATEGORY_RECORDER_CODE);
                    organDeviceRelVO.setDeptId(deptId);
                    organDeviceRelVO.setTenantId(customerCode);
                    organDeviceRelVO.setIndustryCode(industryCode);
                    organDeviceRelVO.setAppVerCode(orderPackages.getApplication().getProductCode());

                    orgList.add(organDeviceRelVO);
                    handleOrgIdList.add(assembleKey);
                }
            }
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(orgList)) {
            orderOrganDeviceRelService.batchSaveDeviceRela(orgList);
        }
    }

    private int getDeviceType(Integer categoryCode) {
        int deviceType = 0;
        if (Objects.equals(OrderConstants.ORDER_RADAR_CODE, categoryCode)) {
            deviceType = OrderConstants.CATEGORY_RADAR_CODE;
        } else if (Objects.equals(OrderConstants.ORDER_SENSOR_CODE, categoryCode)) {
            deviceType = OrderConstants.CATEGORY_SENSOR_CODE;
        }
        return deviceType;
    }

    /**
     * 卫星数据挂组织  vm_organ_street_rel_t 表
     */
    private void addOrganStreet(String customerCode, List<OrderGatewayDTO.OrderPackages> packageList, Map<String, String> industryDeptMap) {
        //新增
        if (!CollectionUtils.isEmpty(packageList)) {
            List<OrganStreetRelVO> orgList = new ArrayList<>();
            List<String> handleOrgList = Lists.newArrayList();
            List<String> industryCodeList = packageList.stream().map(x -> x.getApplication().getAppCode()).distinct().collect(Collectors.toList());
            // 卫星服务区县编码列表
            Set<String> areaCodeSet = Sets.newHashSet();

            for (OrderPackages orderPackages : packageList) {
                List<OrderGatewayDTO.Service> serviceList = orderPackages.getServices();
                String deptId = industryDeptMap.get(orderPackages.getApplication().getAppCode());
                if (!CollectionUtils.isEmpty(serviceList) && StringUtils.isNotEmpty(deptId)) {
                    for (OrderGatewayDTO.Service service : serviceList) {
                        if (StringUtils.isEmpty(service.getAreaCodes())
                                || !Objects.equals(OrderConstants.CATEGORY_SATELLITE_SERVICE_CODE, service.getServiceCode())) {
                            continue;
                        }
                        String[] areaCodeArr = service.getAreaCodes().split(",");
                        if (areaCodeArr.length > 0) {
                            areaCodeSet.addAll(Arrays.asList(areaCodeArr));
                        }
                    }
                }
            }

            // key -> industryCode values -> countyCodeList
            Map<String, Set<String>> existOrganStreetMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(areaCodeSet) && CollectionUtils.isNotEmpty(industryCodeList)) {
                List<OrganStreetRelVO> existOrganStreetList = orderOrganDeviceRelService.selectOrganStreetInfoByCondition(customerCode,
                        OrderConstants.CATEGORY_SATELLITE_CODE, industryCodeList, Lists.newArrayList(areaCodeSet));
                if (CollectionUtils.isNotEmpty(existOrganStreetList)) {
                    for (OrganStreetRelVO organStreetInfo : existOrganStreetList) {
                        String industryCode = organStreetInfo.getIndustryCode();
                        if (existOrganStreetMap.containsKey(industryCode)) {
                            existOrganStreetMap.get(industryCode).add(organStreetInfo.getCountyCode());
                        } else {
                            existOrganStreetMap.put(industryCode, Sets.newHashSet(organStreetInfo.getCountyCode()));
                        }
                    }
                }
            }

            // 行政区域map key -> 区县编码 value -> 行政区域信息
            Map<String, SysAreaVO> regionMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(areaCodeSet)) {
                String areaCodes = Joiner.on(",").join(areaCodeSet);
                Result<List<SysAreaVO>> listResult = systemFeignClient.getAreaStreet(areaCodes);
                if (listResult != null && CollectionUtils.isNotEmpty(listResult.getData())) {
                    regionMap = listResult.getData().stream().collect(Collectors.toMap(SysAreaVO::getRegionCode, (p) -> p, (a, b) -> a));
                }
            }

            for (OrderPackages orderPackages : packageList) {
                List<OrderGatewayDTO.Service> serviceList = orderPackages.getServices();
                String deptId = industryDeptMap.get(orderPackages.getApplication().getAppCode());
                String industryCode = orderPackages.getApplication().getAppCode();
                Set<String> existAreaCodeSet = existOrganStreetMap.get(industryCode);
                if (!CollectionUtils.isEmpty(serviceList) && StringUtils.isNotEmpty(deptId)) {
                    for (OrderGatewayDTO.Service service : serviceList) {
                        if (StringUtils.isEmpty(service.getAreaCodes())
                                || !Objects.equals(OrderConstants.CATEGORY_SATELLITE_SERVICE_CODE, service.getServiceCode())) {
                            continue;
                        }
                        String[] areaCodeArr = service.getAreaCodes().split(",");

                        for (String areaCode : areaCodeArr) {
                            if (existAreaCodeSet != null && existAreaCodeSet.contains(areaCode)) {
                                continue;
                            }
                            String assembleKey = String.format("%s_%s", industryCode, areaCode);
                            if (handleOrgList.contains(assembleKey)) {
                                continue;
                            }
                            //区域调君南接口 areaCode
                            try {
                                handleOrgList.add(assembleKey);
//                                Result<List<SysAreaVO>> listResult = systemFeignClient.getAreaStreet(areaCode);
//                                List<SysAreaVO> sysAreaVOList = listResult.getData();
                                SysAreaVO sysAreaVO = regionMap.get(areaCode);
                                if (sysAreaVO != null) {
                                    for (String streetCode : sysAreaVO.getStreetCodes()) {
                                        OrganStreetRelVO organStreetRelVO = new OrganStreetRelVO();
                                        organStreetRelVO.setCreateTime(DateUtils.getNowDate());
                                        organStreetRelVO.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL_CHAR);
                                        organStreetRelVO.setUpdateTime(DateUtils.getNowDate());
                                        organStreetRelVO.setCategoryCode(OrderConstants.CATEGORY_SATELLITE_CODE);
                                        organStreetRelVO.setDeptId(deptId);
                                        organStreetRelVO.setTenantId(customerCode);
                                        organStreetRelVO.setProvinceCode(sysAreaVO.getProvinceCode());
                                        organStreetRelVO.setCityCode(sysAreaVO.getCityCode());
                                        organStreetRelVO.setCountyCode(areaCode);
                                        organStreetRelVO.setStreetCode(streetCode);
                                        organStreetRelVO.setIndustryCode(industryCode);
                                        organStreetRelVO.setAppVerCode(orderPackages.getApplication().getProductCode());

                                        orgList.add(organStreetRelVO);
                                    }
                                }
                            } catch (Exception e) {
                                log.error("根据区县获取省市区县街道失败", e);
                            }
                        }
                    }
                }
            }
            if (!CollectionUtils.isEmpty(orgList)) {
                orderOrganDeviceRelService.batchSaveStreetRela(orgList);
            }
        }
    }
}
