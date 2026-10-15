package com.chinatower.order.handler.deal;

import com.alibaba.nacos.common.utils.MapUtils;
import com.chinatower.common.core.domain.OrderDeviceDO;
import com.chinatower.common.core.domain.OrderDeviceHisDO;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.order.dto.DeviceIndustryDTO;
import com.chinatower.order.repository.IOrderAlgorithmsRepository;
import com.chinatower.order.repository.IOrderDeviceHisRepository;
import com.chinatower.order.repository.IOrderDeviceRepository;
import com.chinatower.order.service.IOrderOrganDeviceRelService;
import com.chinatower.order.service.IOrderVideoDeviceService;
import com.chinatower.order.vo.AlgorithmByOrderVO;
import com.chinatower.order.vo.AlgorithmOneVO;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.device.SaveOrUpdateChannelDTO;
import com.chinatower.platform.entity.device.SaveOrUpdateDeviceDTO;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ChangeOrderDeviceSetDeal
 * @Deacription 变更订单设备配置关系处理模块
 * @Author chenmf
 * @Date 2022/1/7 10:58
 * @Version 1.0
 **/
@Component
@Slf4j
public class ChangeOrderDeviceSetDeal extends AbstractOrderDeal {

    @Autowired
    private IOrderDeviceHisRepository orderDeviceHisRepository;

    @Autowired
    private IOrderDeviceRepository orderDeviceRepository;

    @Autowired
    private IOrderVideoDeviceService orderVideoDeviceService;

    @Autowired
    private IOrderAlgorithmsRepository orderAlgorithmsRepository;

    @Autowired
    private IOrderOrganDeviceRelService orderOrganDeviceRelService;

    private static final int BATCH_SIZE = 5000;


    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {
        log.info("变更订单设备配置关系处理模块");

        String customerCode = orderGateway.getCustomer().getCustomerCode();
        String orderCode = orderGateway.getOrderCode();
        int orderStatus = orderGateway.getStatus();

        //查询最新订购的设备信息
        List<OrderDeviceDO> orderDeviceList = orderDeviceRepository.getOrderDeviceByOrderCode(orderCode, null);
        if (!CollectionUtils.isEmpty(orderDeviceList)) {
            log.info("变更订单设备配置关系处理模块 1.1");
            //获取迁移次数
            int moveSign = orderDeviceList.get(0).getMoveSign() - 1;
            List<OrderDeviceHisDO> oldOrderDeviceList = orderDeviceHisRepository.getOldOrderDeviceByOrderCode(orderCode, moveSign);
            log.info("变更订单设备配置关系处理模块 1.1.5");
            Set<String> orderDeviceKeyList = new HashSet<>();
            for (OrderDeviceDO x : orderDeviceList) {
                orderDeviceKeyList.add(x.getDeviceCode() + "_" + x.getIndustryCode());
            }

            List<OrderDeviceHisDO> reduceDevList = new ArrayList<>();
            for (OrderDeviceHisDO item : oldOrderDeviceList) {
                String deviceHisKey = item.getDeviceCode() + "_" + item.getIndustryCode();
                if (!orderDeviceKeyList.contains(deviceHisKey)) {
                    reduceDevList.add(item);
                }
            }

            log.info("变更订单设备配置关系处理模块 1.2");
            //减少的设备处理
            reduceDeviceDeal(orderGateway, reduceDevList, customerCode, orderCode, orderStatus);
            log.info("变更订单设备配置关系处理模块 1.3");
            //新增的设备处理
            addDeviceDeal(oldOrderDeviceList, orderGateway);
            // 设置需要退订的准备信息，用于事务外的设备处理
            orderGateway.setReduceDeviceList(reduceDevList);
            log.info("变更订单设备配置关系处理模块 1.4");
        }

        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }
    }

    /**
     * 新增的设备处理
     *
     * @return
     * @author chenmf
     * @date 2022/2/14
     * @version
     */
    private void addDeviceDeal(List<OrderDeviceHisDO> oldOrderDeviceList, OrderGatewayDTO orderGateway) {
        log.info("addDeviceDeal 开始");
        String customerCode = orderGateway.getCustomer().getCustomerCode();
        List<OrderGatewayDTO.OrderPackages> packageList = orderGateway.getPackages();
        if (!CollectionUtils.isEmpty(packageList)) {
            // 设备判重列表，执行过相同设备就不在执行了
            List<String> handleDeviceCodeList = Lists.newArrayList();
            // 渠道判重列表，执行过相同渠道就不在执行了
            List<String> handleChannelCodeList = Lists.newArrayList();
            // 组织关联判重列表，执行过相同组织关联就不在执行了
            List<String> handleOrgIdList = Lists.newArrayList();
            // 真正处理的设备列表
            List<SaveOrUpdateDeviceDTO> deviceDTOList = new ArrayList<>();
            // 真正处理的渠道列表
            List<SaveOrUpdateChannelDTO> channelDTOList = new ArrayList<>();
            // 真正处理的设备关系
            List<OrganDeviceRelVO> orgList = new ArrayList<>();

            Map<String, List<OrderDeviceHisDO>> oldDeviceMap = oldOrderDeviceList.stream()
                    .collect(Collectors.groupingBy(x -> x.getDeviceCode() + "_" + x.getIndustryCode()));

            // 行业编码列表
            List<String> industryCodeList = packageList.stream().map(x -> x.getApplication().getAppCode()).distinct().collect(Collectors.toList());
            // 行业根节点组织map key -> 行业编码 value -> 根节点组织编码
            Map<String, String> industryDeptMap = getRootDeptMap(customerCode, industryCodeList);

            Set<String> deviceCodeSet = Sets.newHashSet();
            for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
                List<OrderGatewayDTO.Device> deviceList = orderPackages.getDevices();
                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(deviceList)) {
                    deviceCodeSet.addAll(deviceList.stream().map(OrderGatewayDTO.Device::getDeviceCode).collect(Collectors.toList()));
                }
            }
            log.info("addDeviceDeal 1.0");
            List<DeviceIndustryDTO> existDeviceInfoList;
            if (deviceCodeSet.size() <= BATCH_SIZE) {
                existDeviceInfoList
                        = orderOrganDeviceRelService.selectExistDeviceCodeList(customerCode, industryCodeList,
                        Lists.newArrayList(deviceCodeSet), Lists.newArrayList(OrderConstants.CATEGORY_DEVICE_CODE));
            }
            else {
                List<String> deviceCodeList = Lists.newArrayList(deviceCodeSet);
                List<List<String>> parDeviceCodeList = Lists.partition(deviceCodeList, BATCH_SIZE);
                List<List<DeviceIndustryDTO>> resultList = ThreadParallelUtil.run(parDeviceCodeList, part ->
                                orderOrganDeviceRelService.selectExistDeviceCodeList(customerCode, industryCodeList,
                                        part, Lists.newArrayList(OrderConstants.CATEGORY_DEVICE_CODE)),
                        ThreadParallelUtil.ConfigFactory.ORDER_DEVICE_DEAL);
                existDeviceInfoList = resultList.stream()
                        .filter(list -> !CollectionUtils.isEmpty(list))
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
            }
            log.info("addDeviceDeal 1.1");

            List<String> existIndustryDeviceCodeList = Lists.newArrayList();
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(existDeviceInfoList)) {
                existIndustryDeviceCodeList = existDeviceInfoList.stream()
                        .map(x -> assembleOrgIdKey(customerCode, x.getIndustryCode(), x.getDeviceCode(), x.getDeviceType()))
                        .distinct().collect(Collectors.toList());
            }

            Map<String, List<OrderGatewayDTO.Device>> addDeviceMap = Maps.newHashMap();

            // 是否私有化
            boolean isDependence = orderVideoDeviceService.isDependenceFlag();

            log.info("addDeviceDeal 1.1.1");

            for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
                String industryCode = orderPackages.getApplication().getAppCode();
                List<OrderGatewayDTO.Device> devList = orderPackages.getDevices();
                Set<String> intersectDevCodeSet = new HashSet<>();
                for (OrderGatewayDTO.Device device : devList) {
                    String deviceKey = device.getDeviceCode() + "_" + industryCode;
                    if (oldDeviceMap.containsKey(deviceKey)) {
                        intersectDevCodeSet.add(device.getDeviceCode());
                    }
                }
                log.info("addDeviceDeal 1.1.1.0");
                List<OrderGatewayDTO.Device> addDevList = new ArrayList<>();
                for (OrderGatewayDTO.Device device : devList) {
                    if (!intersectDevCodeSet.contains(device.getDeviceCode())) {
                        addDevList.add(device);
                    }
                }
                log.info("addDeviceDeal 1.1.1.1");

                if (!CollectionUtils.isEmpty(addDevList)) {
                    addDeviceMap.put(industryCode, addDevList);
                    String deptId = industryDeptMap.get(industryCode);

                    for (OrderGatewayDTO.Device device : addDevList) {
                        // 私有化环境不操作设备、通道表
                        if (!isDependence) {
                            if (!handleDeviceCodeList.contains(device.getDeviceCode())) {
                                SaveOrUpdateDeviceDTO videoDTO = new SaveOrUpdateDeviceDTO();
                                BeanUtils.copyProperties(device, videoDTO);
                                videoDTO.setDeviceName(device.getDevName());
                                videoDTO.setDeviceStatus(device.getStatus());
                                deviceDTOList.add(videoDTO);
                                handleDeviceCodeList.add(device.getDeviceCode());
                            } else {
                                log.info("ChangeOrderDeviceSetDeal addDeviceDeal device orderCode:{}, deviceCode:{} 该订单已经处理了该设备，不再重复处理",
                                        orderGateway.getOrderCode(), device.getDeviceCode());
                            }
                            List<OrderGatewayDTO.DeviceChannels> deviceChannelList = device.getChannels();
                            if(!CollectionUtils.isEmpty(deviceChannelList)) {
                                for (OrderGatewayDTO.DeviceChannels deviceChannels : deviceChannelList) {
                                    if (handleChannelCodeList.contains(deviceChannels.getChannelCode())) {
                                        log.info("ChangeOrderDeviceSetDeal addDeviceDeal channel orderCode:{}, channelCode:{} 该订单已经处理了该渠道，不再重复处理",
                                                orderGateway.getOrderCode(), deviceChannels.getChannelCode());
                                        continue;
                                    }
                                    SaveOrUpdateChannelDTO channelDTO = new SaveOrUpdateChannelDTO();
                                    BeanUtils.copyProperties(deviceChannels, channelDTO);
                                    channelDTO.setDeviceCode(device.getDeviceCode());
                                    channelDTO.setChannelStatus(deviceChannels.getStatus());
                                    channelDTOList.add(channelDTO);
                                    handleChannelCodeList.add(deviceChannels.getChannelCode());
                                }
                            }
                        }

                        // 增加判断，设备没有关联该租户、该行业的组织才去绑定
                        String assembleKey = assembleOrgIdKey(customerCode, industryCode, device.getDeviceCode(),
                                OrderConstants.CATEGORY_DEVICE_CODE);
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
                            organDeviceRelVO.setDeviceType(OrderConstants.CATEGORY_DEVICE_CODE);
                            organDeviceRelVO.setIndustryCode(industryCode);
                            organDeviceRelVO.setAppVerCode(orderPackages.getApplication().getProductCode());
                            orgList.add(organDeviceRelVO);
                            handleOrgIdList.add(assembleKey);
                        }
                    }
                }
            }
            orderGateway.setAddDeviceMap(addDeviceMap);

            log.info("addDeviceDeal orgList:{}, deviceDTOList:{}, channelDTOList:{}", orgList.size(), deviceDTOList.size(), channelDTOList.size());

            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(orgList)) {
                //批量保存设备组织关系
                orderOrganDeviceRelService.batchSaveDeviceRela(orgList);
            }
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(deviceDTOList)) {
                //批量保存和更新设备信息
                orderVideoDeviceService.batchSaveOrUpdateDevice(deviceDTOList);
            }
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(channelDTOList)) {
                //批量保存和更新设备通道信息
                orderVideoDeviceService.batchSaveOrUpdateChannel(channelDTOList);
            }

            log.info("addDeviceDeal 结束");
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
     * 减少的设备处理
     *
     * @return
     * @author chenmf
     * @date 2022/2/14
     * @version
     */
    private void reduceDeviceDeal(OrderGatewayDTO orderGateway, List<OrderDeviceHisDO> reduceDevList,
                                  String customerCode, String orderCode, int orderStatus) {
        Integer orderType = orderGateway.getOrderType();
        // 非精准转非精准订单，保留原有逻辑
        if (orderType == null || Objects.equals(OrderConstants.ORDER_TYPE_NON_PRECISE_TO_NON_PRECISE, orderType)) {
            dealReduceForNonPrecise(reduceDevList, customerCode, orderCode, orderStatus);
        }
        // 非精准转精准、精准转精准订单，停用 algorithmConfigList 之外的算法配置
        else if (Objects.equals(OrderConstants.ORDER_TYPE_NON_PRECISE_TO_PRECISE, orderType)
                || Objects.equals(OrderConstants.ORDER_TYPE_PRECISE_TO_PRECISE, orderType)) {
            dealReduceForAlgorithmConfig(orderGateway, customerCode, orderCode);
        }
        // 精准转非精准订单，停用 orderPackages.devices 之外的设备算法配置
        else if (Objects.equals(OrderConstants.ORDER_TYPE_PRECISE_TO_NON_PRECISE, orderType)) {
            dealReduceForPackages(orderGateway, customerCode, orderCode);
        }
    }

    private void dealReduceForNonPrecise(List<OrderDeviceHisDO> reduceDevList, String customerCode,
                                         String orderCode, int orderStatus) {
        if (!CollectionUtils.isEmpty(reduceDevList)) {

            Map<String, Set<String>> industryDeviceMap = Maps.newHashMap();
            for (OrderDeviceHisDO orderDeviceHisDO : reduceDevList) {
                String industryCode = orderDeviceHisDO.getIndustryCode();
                if (industryDeviceMap.containsKey(industryCode)) {
                    industryDeviceMap.get(industryCode).add(orderDeviceHisDO.getDeviceCode());
                } else {
                    industryDeviceMap.put(industryCode, Sets.newHashSet(orderDeviceHisDO.getDeviceCode()));
                }
            }

            if (MapUtils.isNotEmpty(industryDeviceMap)) {
                List<String> industryCodeList= industryDeviceMap.keySet().stream().distinct().collect(Collectors.toList());
                List<String> deviceCodeList = industryDeviceMap.values().stream().flatMap(Set::stream).distinct().collect(Collectors.toList());

                if (deviceCodeList.size() <= BATCH_SIZE) {
                    // 订单退订，删除算法配置，下发删除指令到6.35接口
                    AlgorithmByOrderVO algorithmByOrderVO = new AlgorithmByOrderVO();
                    algorithmByOrderVO.setCustomerCode(customerCode);
                    algorithmByOrderVO.setOrderCode(orderCode);
                    algorithmByOrderVO.setDeviceCodeList(deviceCodeList);
                    algorithmByOrderVO.setOrdStatus(orderStatus);
                    algorithmByOrderVO.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL);
                    algorithmByOrderVO.setIndustryCodeList(industryCodeList);
                    //算法停用并通知算法仓
                    List<AlgorithmOneVO> areaList = orderAlgorithmsRepository.getAlgorithmAreaByOrder(algorithmByOrderVO);
                    // 匹配到需要更改的配置id列表
                    List<String> configIdList = areaList.stream().filter(x -> industryDeviceMap.containsKey(x.getIndustryCode())
                                    && industryDeviceMap.get(x.getIndustryCode()).contains(x.getDeviceCode()))
                            .map(AlgorithmOneVO::getConfigId).distinct().collect(Collectors.toList());
                    orderAlgorithmsRepository.updateAlgorithmStatusByConfigId(DeviceConstants.ALGORITHM_STATUS_STOP, configIdList);

                }
                else {
                    List<AlgorithmOneVO> areaList = Lists.newArrayList();
                    Lists.partition(deviceCodeList, BATCH_SIZE).forEach(subDeviceCodeList -> {
                        // 订单退订，删除算法配置，下发删除指令到6.35接口
                        AlgorithmByOrderVO algorithmByOrderVO = new AlgorithmByOrderVO();
                        algorithmByOrderVO.setCustomerCode(customerCode);
                        algorithmByOrderVO.setOrderCode(orderCode);
                        algorithmByOrderVO.setDeviceCodeList(subDeviceCodeList);
                        algorithmByOrderVO.setOrdStatus(orderStatus);
                        algorithmByOrderVO.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL);
                        algorithmByOrderVO.setIndustryCodeList(industryCodeList);
                        //算法停用并通知算法仓
                        List<AlgorithmOneVO> subAreaList = orderAlgorithmsRepository.getAlgorithmAreaByOrder(algorithmByOrderVO);
                        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(subAreaList)) {
                            areaList.addAll(subAreaList);
                        }
                    });

                    // 匹配到需要更改的配置id列表
                    List<String> configIdList = areaList.stream().filter(x -> industryDeviceMap.containsKey(x.getIndustryCode())
                                    && industryDeviceMap.get(x.getIndustryCode()).contains(x.getDeviceCode()))
                            .map(AlgorithmOneVO::getConfigId).distinct().collect(Collectors.toList());
                    updateStopConfigInBatch(configIdList);
                }
            }
        }
    }

    /**
     * 非精准转精准、精准转精准订单：
     * 根据 algorithmConfigList 中算法类型为摄像机(1)的设备编码、算法编码，停用 vm_algorithm_config 中不匹配的配置
     */
    private void dealReduceForAlgorithmConfig(OrderGatewayDTO orderGateway, String customerCode, String orderCode) {
        List<OrderGatewayDTO.AlgorithmConfigInfo> algorithmConfigList = orderGateway.getAlgorithmConfigList();
        // 仅处理摄像机算法配置；目标算法为空时，需将该订单的所有算法配置都停用
        Set<String> algorithmDevicePairSet = org.apache.commons.collections4.CollectionUtils.isEmpty(algorithmConfigList)
                ? Collections.emptySet()
                : algorithmConfigList.stream()
                        .filter(x -> Objects.equals(OrderConstants.ALGORITHM_CONFIG_TYPE_CAMERA, x.getAlgorithmType()))
                        .map(x -> buildAlgorithmDeviceKey(x.getDeviceCode(), x.getAlgorithmCode()))
                        .collect(Collectors.toSet());

        List<AlgorithmOneVO> areaList = orderAlgorithmsRepository.getAlgorithmAreaByOrder(buildBaseQuery(customerCode, orderCode));
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(areaList)) {
            // 停用：设备编码、算法编码不为 algorithmConfigList 中数据的配置；目标算法为空时停用全部
            List<String> configIdList = areaList.stream()
                    .filter(x -> !algorithmDevicePairSet.contains(buildAlgorithmDeviceKey(x.getDeviceCode(), x.getAlgorithmCode())))
                    .map(AlgorithmOneVO::getConfigId).distinct().collect(Collectors.toList());
            updateStopConfigInBatch(configIdList);
        }
    }

    /**
     * 精准转非精准订单：
     * 根据 orderPackages.devices 中的设备编码，停用 vm_algorithm_config 中不匹配的配置
     */
    private void dealReduceForPackages(OrderGatewayDTO orderGateway, String customerCode, String orderCode) {
        List<OrderGatewayDTO.OrderPackages> packageList = orderGateway.getPackages();
        // 套餐中的设备编码；目标设备为空时，需将该订单的所有算法配置都停用
        Set<String> packageDeviceCodeSet = org.apache.commons.collections4.CollectionUtils.isEmpty(packageList)
                ? Collections.emptySet()
                : packageList.stream()
                        .filter(x -> org.apache.commons.collections4.CollectionUtils.isNotEmpty(x.getDevices()))
                        .flatMap(x -> x.getDevices().stream())
                        .map(OrderGatewayDTO.Device::getDeviceCode)
                        .collect(Collectors.toSet());

        List<AlgorithmOneVO> areaList = orderAlgorithmsRepository.getAlgorithmAreaByOrder(buildBaseQuery(customerCode, orderCode));
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(areaList)) {
            // 停用：设备编码不为 orderPackages.devices 中数据的配置；目标设备为空时停用全部
            List<String> configIdList = areaList.stream()
                    .filter(x -> !packageDeviceCodeSet.contains(x.getDeviceCode()))
                    .map(AlgorithmOneVO::getConfigId).distinct().collect(Collectors.toList());
            updateStopConfigInBatch(configIdList);
        }
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

    private void updateStopConfigInBatch(List<String> configIdList) {
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(configIdList)) {
            return;
        }
        if (configIdList.size() <= BATCH_SIZE) {
            orderAlgorithmsRepository.updateAlgorithmStatusByConfigId(DeviceConstants.ALGORITHM_STATUS_STOP, configIdList);
        } else {
            Lists.partition(configIdList, BATCH_SIZE).forEach(subConfigIdList -> {
                orderAlgorithmsRepository.updateAlgorithmStatusByConfigId(DeviceConstants.ALGORITHM_STATUS_STOP, subConfigIdList);
            });
        }
    }

}
