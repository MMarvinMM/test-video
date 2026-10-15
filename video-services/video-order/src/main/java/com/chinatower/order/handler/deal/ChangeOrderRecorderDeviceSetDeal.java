package com.chinatower.order.handler.deal;

import com.alibaba.nacos.common.utils.MapUtils;
import com.chinatower.common.core.domain.*;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.order.domain.OrganDeviceRelDO;
import com.chinatower.order.dto.DeviceIndustryDTO;
import com.chinatower.order.repository.*;
import com.chinatower.order.service.IOrderOrganDeviceRelService;
import com.chinatower.order.service.IOrderVideoDeviceService;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.device.DelDeviceRelaDTO;
import com.chinatower.platform.entity.device.SaveOrUpdateChannelDTO;
import com.chinatower.platform.entity.device.SaveOrUpdateDeviceDTO;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
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

@Component
@Slf4j
public class ChangeOrderRecorderDeviceSetDeal extends AbstractOrderDeal {

    @Autowired
    private IOrderRecorderDeviceRepository orderRecorderDeviceRepository;

    @Autowired
    private IOrderRecorderDeviceHisRepository orderRecorderDeviceHisRepository;

    @Autowired
    private IOrderVideoDeviceService orderVideoDeviceService;

    @Autowired
    private IOrderOrganDeviceRelService orderOrganDeviceRelService;

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {
        log.info("变更订单设备配置关系处理模块");
        String orderCode = orderGateway.getOrderCode();
        List<String> orderCodeList = Lists.newArrayList();
        orderCodeList.add(orderCode);
        //查询最新订购的设备信息
        List<OrderRecorderDeviceDO> orderDeviceList = orderRecorderDeviceRepository.getOrderDeviceByList(orderCodeList, null);
        if (!CollectionUtils.isEmpty(orderDeviceList)) {

            //获取迁移次数
            int moveSign = orderDeviceList.get(0).getMoveSign() - 1;
            List<OrderRecorderDeviceHisDO> oldOrderDeviceList = orderRecorderDeviceHisRepository.getOldOrderDeviceByOrderCode(orderCode, moveSign);

            List<String> orderDeviceKeyList = orderDeviceList.stream().map(x -> (x.getDeviceCode() + "_" + x.getIndustryCode())).collect(Collectors.toList());

            //需要退订的设备
            List<OrderRecorderDeviceHisDO> reduceDevList = oldOrderDeviceList.stream().filter(
                    item -> !orderDeviceKeyList.contains(item.getDeviceCode() + "_" + item.getIndustryCode())).collect(Collectors.toList());

            //减少的设备处理
            reduceDeviceDeal(reduceDevList);
            //新增的设备处理
            addDeviceDeal(oldOrderDeviceList, orderGateway);
        } else {
            //获取迁移次数
            int moveSign = orderGateway.getOrderMoveSign() - 1;
            List<OrderRecorderDeviceHisDO> reduceDevList = orderRecorderDeviceHisRepository.getOldOrderDeviceByOrderCode(orderCode, moveSign);
            if (!CollectionUtils.isEmpty(reduceDevList)) {
                // 减少的设备处理
                reduceDeviceDeal(reduceDevList);
            }
        }

        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }
    }

    private void addDeviceDeal(List<OrderRecorderDeviceHisDO> oldOrderDeviceList, OrderGatewayDTO orderGateway) {

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

            Map<String, List<OrderRecorderDeviceHisDO>> oldDeviceMap = oldOrderDeviceList.stream()
                    .collect(Collectors.groupingBy(x -> x.getDeviceCode() + "_" + x.getIndustryCode()));

            // 行业编码列表
            List<String> industryCodeList = packageList.stream().map(x -> x.getApplication().getAppCode()).distinct().collect(Collectors.toList());
            // 行业根节点组织map key -> 行业编码 value -> 根节点组织编码
            Map<String, String> industryDeptMap = getRootDeptMap(customerCode, industryCodeList);

            Set<String> deviceCodeSet = Sets.newHashSet();
            for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
                List<OrderGatewayDTO.Device> deviceList = orderPackages.getRecorderDevice();
                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(deviceList)) {
                    deviceCodeSet.addAll(deviceList.stream().map(OrderGatewayDTO.Device::getDeviceCode).collect(Collectors.toList()));
                }
            }

            List<DeviceIndustryDTO> existDeviceInfoList
                    = orderOrganDeviceRelService.selectExistDeviceCodeList(customerCode, industryCodeList,
                    Lists.newArrayList(deviceCodeSet), Lists.newArrayList(OrderConstants.CATEGORY_RECORDER_CODE));
            List<String> existIndustryDeviceCodeList = Lists.newArrayList();
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(existDeviceInfoList)) {
                existIndustryDeviceCodeList = existDeviceInfoList.stream()
                        .map(x -> assembleOrgIdKey(customerCode, x.getIndustryCode(), x.getDeviceCode(), x.getDeviceType()))
                        .distinct().collect(Collectors.toList());
            }

            Map<String, List<OrderGatewayDTO.Device>> addDeviceMap = Maps.newHashMap();

            for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
                String industryCode = orderPackages.getApplication().getAppCode();
                List<OrderGatewayDTO.Device> devList = orderPackages.getRecorderDevice();
                //交集（更新套餐ID）
                List<OrderGatewayDTO.Device> intersectDev = devList.stream().filter(item ->
                        oldDeviceMap.containsKey(item.getDeviceCode() + "_" + industryCode)).collect(Collectors.toList());

                //需要新增的设备
                List<OrderGatewayDTO.Device> addDevList = devList.stream().filter(
                        item -> !intersectDev.stream().map(OrderGatewayDTO.Device::getDeviceCode).collect(Collectors.toList()).
                                contains(item.getDeviceCode())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(addDevList)) {
                    addDeviceMap.put(industryCode, addDevList);
                    String deptId = industryDeptMap.get(industryCode);

                    for (OrderGatewayDTO.Device device : addDevList) {
                        if (!handleDeviceCodeList.contains(device.getDeviceCode())) {
                            SaveOrUpdateDeviceDTO videoDTO = new SaveOrUpdateDeviceDTO();
                            BeanUtils.copyProperties(device, videoDTO);
                            videoDTO.setDeviceName(device.getDevName());
                            videoDTO.setDeviceStatus(device.getStatus());
                            deviceDTOList.add(videoDTO);
                            handleDeviceCodeList.add(device.getDeviceCode());
                        } else {
                            log.info("ChangeOrderRecorderDeviceSetDeal addDeviceDeal device orderCode:{}, deviceCode:{} 该订单已经处理了该设备，不再重复处理",
                                    orderGateway.getOrderCode(), device.getDeviceCode());
                        }
                        List<OrderGatewayDTO.DeviceChannels> deviceChannelList = device.getChannels();
                        if(!CollectionUtils.isEmpty(deviceChannelList)) {
                            for (OrderGatewayDTO.DeviceChannels deviceChannels : deviceChannelList) {
                                if (handleChannelCodeList.contains(deviceChannels.getChannelCode())) {
                                    log.info("ChangeOrderRecorderDeviceSetDeal addDeviceDeal channel orderCode:{}, channelCode:{} 该订单已经处理了该渠道，不再重复处理",
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

                        // 增加判断，设备没有关联该租户、该行业的组织才去绑定
                        String assembleKey = assembleOrgIdKey(customerCode, industryCode, device.getDeviceCode(), OrderConstants.CATEGORY_RECORDER_CODE);
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
                            organDeviceRelVO.setDeviceType(OrderConstants.CATEGORY_RECORDER_CODE);
                            organDeviceRelVO.setIndustryCode(industryCode);
                            organDeviceRelVO.setAppVerCode(orderPackages.getApplication().getProductCode());
                            orgList.add(organDeviceRelVO);
                            handleOrgIdList.add(assembleKey);
                        }
                    }
                }
            }
            orderGateway.setAddRecordDeviceMap(addDeviceMap);
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(deviceDTOList)) {
                //批量保存和更新设备信息
                orderVideoDeviceService.batchSaveOrUpdateRecorderDevice(deviceDTOList);
            }
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(channelDTOList)) {
                //批量保存和更新设备通道信息
                orderVideoDeviceService.batchSaveOrUpdateRecorderChannel(channelDTOList);
            }
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(orgList)) {
                //批量保存设备组织关系
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

    private void reduceDeviceDeal(List<OrderRecorderDeviceHisDO> reduceDevList) {
        if (!CollectionUtils.isEmpty(reduceDevList)) {
            String tenantId = reduceDevList.get(0).getTenantId();
            Map<String, Set<String>> industryDeviceMap = Maps.newHashMap();
            for (OrderRecorderDeviceHisDO orderRecorderDeviceHisDO : reduceDevList) {
                String industryCode = orderRecorderDeviceHisDO.getIndustryCode();
                if (industryDeviceMap.containsKey(industryCode)) {
                    industryDeviceMap.get(industryCode).add(orderRecorderDeviceHisDO.getDeviceCode());
                } else {
                    industryDeviceMap.put(industryCode, Sets.newHashSet(orderRecorderDeviceHisDO.getDeviceCode()));
                }
            }

            if (MapUtils.isNotEmpty(industryDeviceMap)) {
                List<String> deviceCodeList = industryDeviceMap.values().stream().flatMap(Set::stream).distinct().collect(Collectors.toList());
                DelDeviceRelaDTO param = new DelDeviceRelaDTO();
                param.setDeviceList(deviceCodeList);
                param.setTenantId(tenantId);
                param.setDeviceType(OrderConstants.CATEGORY_RECORDER_CODE);
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
        }
    }

}
