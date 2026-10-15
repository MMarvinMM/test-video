package com.chinatower.order.handler.deal;

import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.device.entity.dto.IotDeviceMsgDTO;
import com.chinatower.order.domain.HornDeviceDO;
import com.chinatower.order.service.IOrderSystemService;
import com.chinatower.order.service.IOrderVideoDeviceService;
import com.chinatower.platform.entity.device.SaveOrUpdateChannelDTO;
import com.chinatower.platform.entity.device.SaveOrUpdateDeviceDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName SaveOrUpdateDeviceDeal
 * @Deacription 保存/更新设备信息
 * @Author chenmf
 * @Date 2022/2/11 9:54
 * @Version 1.0
 **/
@Slf4j
@Component
public class SaveOrUpdateDeviceDeal extends AbstractOrderDeal {

    @Autowired
    private IOrderVideoDeviceService orderVideoDeviceService;
    @Autowired
    private IOrderSystemService orderSystemService;


    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {
        log.info("保存/更新设备信息");
        List<OrderGatewayDTO.OrderPackages> packageList = orderGateway.getPackages();
        if (!CollectionUtils.isEmpty(packageList)) {
            //保存/更新设备信息
            saveOrUpdateDeviceDeal(packageList, orderGateway);
            //保存/更新物联设备信息
            saveOrUpdateIotDeviceDeal(packageList, orderGateway);
            //保存/更新无人机设备信息
            saveOrUpdateUavDeviceDeal(packageList, orderGateway);
            // 保存/更新无人机机巢设备信息
            saveOrUpdateUavNestDeviceDeal(packageList, orderGateway);
            //保存/更新喇叭设备信息
            saveOrUpdateHornDeviceDeal(packageList, orderGateway);
            //保存/更新执法记录仪设备信息
            saveOrUpdateRecorderDeviceDeal(packageList, orderGateway);
        }
        //保存角色信息
        saveRole(orderGateway);

        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }
    }

    /**
     * 保存结角色信息
     */
    private void saveRole(OrderGatewayDTO orderGateway) {
        log.info("=======保存角色信息 初始化告警流程配置=========={}",orderGateway.getOrderCode());
        OrderGatewayDTO.Customer customer = orderGateway.getCustomer();
        String userId = orderSystemService.findUserIdByUserName(customer.getAdmin());
        List<OrderGatewayDTO.OrderPackages> packages = orderGateway.getPackages();
        Map<String, String> industryMap = packages.stream()
                .collect(Collectors.toMap(x -> x.getApplication().getAppCode(), x -> x.getApplication().getProductCode(), (a, b) -> a));
        orderSystemService.insertUserRoleBatch(userId, industryMap);
    }

    /**
     * 设备批量保存修改
     */
    private void saveOrUpdateDeviceDeal(List<OrderGatewayDTO.OrderPackages> packageList, OrderGatewayDTO orderGateway) {
        // 设备判重列表，执行过相同设备就不在执行了
        List<String> deviceCodeList = Lists.newArrayList();
        // 渠道判重列表，执行过相同渠道就不在执行了
        List<String> channelCodeList = Lists.newArrayList();
        // 需要处理的设备列表
        List<SaveOrUpdateDeviceDTO> deviceDTOList = new ArrayList<>();
        // 需要处理的渠道列表
        List<SaveOrUpdateChannelDTO> channelDTOList = new ArrayList<>();

        // 是否私有化
        boolean isDependence = orderVideoDeviceService.isDependenceFlag();

        Map<String, List<OrderGatewayDTO.Device>> addDeviceMap = Maps.newHashMap();
        for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
            String industryCode = orderPackages.getApplication().getAppCode();
            List<OrderGatewayDTO.Device> deviceList = orderPackages.getDevices();
            if (!CollectionUtils.isEmpty(deviceList)) {
                addDeviceMap.put(industryCode, deviceList);
                log.info("deviceList:" + deviceList.size());
                for (OrderGatewayDTO.Device device : deviceList) {
                    // 私有化环境不新增设备、通道信息
                    if (!isDependence) {
                        if (!deviceCodeList.contains(device.getDeviceCode())) {
                            SaveOrUpdateDeviceDTO videoDTO = new SaveOrUpdateDeviceDTO();
                            BeanUtils.copyProperties(device, videoDTO);
                            videoDTO.setDeviceName(device.getDevName());
                            videoDTO.setDeviceStatus(device.getStatus());

                            deviceDTOList.add(videoDTO);
                            deviceCodeList.add(device.getDeviceCode());
                        } else {
                            log.info("SaveOrUpdateDeviceDeal saveOrUpdateDeviceDeal device orderCode:{}, deviceCode:{} 该订单已经处理了该设备，不再重复处理",
                                    orderGateway.getOrderCode(), device.getDeviceCode());
                        }

                        List<OrderGatewayDTO.DeviceChannels> deviceChannelList = device.getChannels();
                        if(!CollectionUtils.isEmpty(deviceChannelList)) {
                            for (OrderGatewayDTO.DeviceChannels deviceChannels : deviceChannelList) {
                                if (channelCodeList.contains(deviceChannels.getChannelCode())) {
                                    log.info("SaveOrUpdateDeviceDeal saveOrUpdateDeviceDeal channel orderCode:{}, channelCode:{} 该订单已经处理了该渠道，不再重复处理",
                                            orderGateway.getOrderCode(), deviceChannels.getChannelCode());
                                    continue;
                                }
                                SaveOrUpdateChannelDTO channelDTO = new SaveOrUpdateChannelDTO();
                                BeanUtils.copyProperties(deviceChannels, channelDTO);
                                channelDTO.setDeviceCode(device.getDeviceCode());
                                channelDTO.setChannelStatus(deviceChannels.getStatus());
                                channelDTOList.add(channelDTO);
                                channelCodeList.add(deviceChannels.getChannelCode());
                            }
                        }
                    }
                }
            }
        }
        orderGateway.setAddDeviceMap(addDeviceMap);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(deviceDTOList)) {
            orderVideoDeviceService.batchSaveOrUpdateDevice(deviceDTOList);
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(channelDTOList)) {
            orderVideoDeviceService.batchSaveOrUpdateChannel(channelDTOList);
        }
    }

    /**
     * 物联网传感器设备批量保存修改
     */
    public void saveOrUpdateIotDeviceDeal(List<OrderGatewayDTO.OrderPackages> packageList, OrderGatewayDTO orderGateway) {
        // 设备判重列表，执行过相同设备就不在执行了
        List<String> deviceCodeList = Lists.newArrayList();
        List<IotDeviceMsgDTO> deviceDTOList = new ArrayList<>();

        Map<String, List<OrderGatewayDTO.SensorDevice>> addDeviceMap = Maps.newHashMap();
        for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
            String industryCode = orderPackages.getApplication().getAppCode();
            List<OrderGatewayDTO.SensorDevice> deviceList = orderPackages.getSensorDevice();
            if (!CollectionUtils.isEmpty(deviceList)) {
                addDeviceMap.put(industryCode, deviceList);
                for (OrderGatewayDTO.SensorDevice device : deviceList) {
                    if (deviceCodeList.contains(device.getDeviceCode())) {
                        log.info("SaveOrUpdateDeviceDeal saveOrUpdateIotDeviceDeal orderCode:{}, deviceCode:{} 该订单已经处理了该物联设备，不再重复处理",
                                orderGateway.getOrderCode(), device.getDeviceCode());
                        continue;
                    }
                    IotDeviceMsgDTO videoDTO = new IotDeviceMsgDTO();
                    BeanUtils.copyProperties(device, videoDTO);
                    videoDTO.setDeviceName(device.getDevName());
                    videoDTO.setDeviceStatus(device.getStatus());
                    videoDTO.setDeviceType(device.getDeviceType());
                    deviceDTOList.add(videoDTO);
                    deviceCodeList.add(device.getDeviceCode());
                }
            }
        }
        orderGateway.setAddSensorDeviceMap(addDeviceMap);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(deviceDTOList)) {
            orderVideoDeviceService.batchSaveOrUpdateIotDevice(deviceDTOList);
        }
    }

    public void saveOrUpdateHornDeviceDeal(List<OrderGatewayDTO.OrderPackages> packageList, OrderGatewayDTO orderGateway) {
        // 设备判重列表，执行过相同设备就不在执行了
        List<String> deviceCodeList = Lists.newArrayList();
        List<HornDeviceDO> deviceInfoList = new ArrayList<>();
        Map<String, List<OrderGatewayDTO.SensorDevice>> addDeviceMap = Maps.newHashMap();
        for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
            String industryCode = orderPackages.getApplication().getAppCode();
            List<OrderGatewayDTO.SensorDevice> deviceList = orderPackages.getHornDevice();
            if (!CollectionUtils.isEmpty(deviceList)) {
                addDeviceMap.put(industryCode, deviceList);
                for (OrderGatewayDTO.SensorDevice device : deviceList) {
                    if (deviceCodeList.contains(device.getDeviceCode())) {
                        log.info("SaveOrUpdateDeviceDeal saveOrUpdateHornDeviceDeal orderCode:{}, deviceCode:{} 该订单已经处理了该喇叭设备，不再重复处理",
                                orderGateway.getOrderCode(), device.getDeviceCode());
                        continue;
                    }
                    HornDeviceDO videoDTO = new HornDeviceDO();
                    BeanUtils.copyProperties(device, videoDTO);
                    videoDTO.setDeviceName(device.getDevName());
                    videoDTO.setDeviceStatus(device.getStatus());
                    videoDTO.setDeviceType(device.getDeviceType());
                    deviceInfoList.add(videoDTO);
                    deviceCodeList.add(device.getDeviceCode());
                }
            }
        }
        orderGateway.setAddHornDeviceMap(addDeviceMap);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(deviceInfoList)) {
            orderVideoDeviceService.batchSaveOrUpdateHornDevice(deviceInfoList);
        }
    }

    private void saveOrUpdateUavDeviceDeal(List<OrderGatewayDTO.OrderPackages> packageList, OrderGatewayDTO orderGateway) {
        // 设备判重列表，执行过相同设备就不在执行了
        List<String> deviceCodeList = Lists.newArrayList();
        // 需要处理的设备列表
        List<SaveOrUpdateDeviceDTO> deviceDTOList = new ArrayList<>();
        Map<String, List<OrderGatewayDTO.Device>> addDeviceMap = Maps.newHashMap();
        for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
            String industryCode = orderPackages.getApplication().getAppCode();
            List<OrderGatewayDTO.Device> deviceList = orderPackages.getUavDevice();
            if (!CollectionUtils.isEmpty(deviceList)) {
                addDeviceMap.put(industryCode, deviceList);
                log.info("uav deviceList:" + deviceList.size());
                for (OrderGatewayDTO.Device device : deviceList) {
                    if (!deviceCodeList.contains(device.getDeviceCode())) {
                        SaveOrUpdateDeviceDTO videoDTO = new SaveOrUpdateDeviceDTO();
                        BeanUtils.copyProperties(device, videoDTO);
                        videoDTO.setDeviceName(device.getDevName());
                        videoDTO.setDeviceStatus(device.getStatus());
                        deviceDTOList.add(videoDTO);
                        deviceCodeList.add(device.getDeviceCode());
                    } else {
                        log.info("SaveOrUpdateDeviceDeal saveOrUpdateUavDeviceDeal device orderCode:{}, deviceCode:{} 该订单已经处理了该设备，不再重复处理",
                                orderGateway.getOrderCode(), device.getDeviceCode());
                    }
                }
            }
        }
        orderGateway.setAddUavDeviceMap(addDeviceMap);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(deviceDTOList)) {
            orderVideoDeviceService.batchSaveOrUpdateUavDevice(deviceDTOList);
        }
    }

    private void saveOrUpdateUavNestDeviceDeal(List<OrderGatewayDTO.OrderPackages> packageList, OrderGatewayDTO orderGateway) {
        // 设备判重列表，执行过相同设备就不在执行了
        List<String> deviceCodeList = Lists.newArrayList();
        // 需要处理的设备列表
        List<SaveOrUpdateDeviceDTO> deviceDTOList = new ArrayList<>();
        Map<String, List<OrderGatewayDTO.Device>> addDeviceMap = Maps.newHashMap();
        for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
            String industryCode = orderPackages.getApplication().getAppCode();
            List<OrderGatewayDTO.Device> deviceList = orderPackages.getNestDevice();
            if (!CollectionUtils.isEmpty(deviceList)) {
                addDeviceMap.put(industryCode, deviceList);
                log.info("uav nest deviceList:" + deviceList.size());
                for (OrderGatewayDTO.Device device : deviceList) {
                    if (!deviceCodeList.contains(device.getDeviceCode())) {
                        SaveOrUpdateDeviceDTO videoDTO = new SaveOrUpdateDeviceDTO();
                        BeanUtils.copyProperties(device, videoDTO);
                        videoDTO.setDeviceName(device.getDevName());
                        videoDTO.setDeviceStatus(device.getStatus());
                        deviceDTOList.add(videoDTO);
                        deviceCodeList.add(device.getDeviceCode());
                    } else {
                        log.info("saveOrUpdateUavNestDeviceDeal saveOrUpdateUavDeviceDeal device orderCode:{}, deviceCode:{} 该订单已经处理了该设备，不再重复处理",
                                orderGateway.getOrderCode(), device.getDeviceCode());
                    }
                }
            }
        }
        orderGateway.setAddUavDeviceMap(addDeviceMap);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(deviceDTOList)) {
            orderVideoDeviceService.batchSaveOrUpdateUavNestDevice(deviceDTOList);
        }
    }

    private void saveOrUpdateRecorderDeviceDeal(List<OrderGatewayDTO.OrderPackages> packageList, OrderGatewayDTO orderGateway) {
        // 设备判重列表，执行过相同设备就不在执行了
        List<String> deviceCodeList = Lists.newArrayList();
        // 渠道判重列表，执行过相同渠道就不在执行了
        List<String> channelCodeList = Lists.newArrayList();
        // 需要处理的设备列表
        List<SaveOrUpdateDeviceDTO> deviceDTOList = new ArrayList<>();
        // 需要处理的渠道列表
        List<SaveOrUpdateChannelDTO> channelDTOList = new ArrayList<>();

        Map<String, List<OrderGatewayDTO.Device>> addDeviceMap = Maps.newHashMap();
        for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
            String industryCode = orderPackages.getApplication().getAppCode();
            List<OrderGatewayDTO.Device> deviceList = orderPackages.getRecorderDevice();
            if (!CollectionUtils.isEmpty(deviceList)) {
                addDeviceMap.put(industryCode, deviceList);
                log.info("recorder deviceList:" + deviceList.size());
                for (OrderGatewayDTO.Device device : deviceList) {
                    if (!deviceCodeList.contains(device.getDeviceCode())) {
                        SaveOrUpdateDeviceDTO videoDTO = new SaveOrUpdateDeviceDTO();
                        BeanUtils.copyProperties(device, videoDTO);
                        videoDTO.setDeviceName(device.getDevName());
                        videoDTO.setDeviceStatus(device.getStatus());
                        deviceDTOList.add(videoDTO);
                        deviceCodeList.add(device.getDeviceCode());
                    } else {
                        log.info("SaveOrUpdateDeviceDeal saveOrUpdateRecorderDeviceDeal device orderCode:{}, deviceCode:{} 该订单已经处理了该设备，不再重复处理",
                                orderGateway.getOrderCode(), device.getDeviceCode());
                    }

                    List<OrderGatewayDTO.DeviceChannels> deviceChannelList = device.getChannels();
                    if(!CollectionUtils.isEmpty(deviceChannelList)) {
                        for (OrderGatewayDTO.DeviceChannels deviceChannels : deviceChannelList) {
                            if (channelCodeList.contains(deviceChannels.getChannelCode())) {
                                log.info("SaveOrUpdateDeviceDeal saveOrUpdateRecorderDeviceDeal channel orderCode:{}, channelCode:{} 该订单已经处理了该渠道，不再重复处理",
                                        orderGateway.getOrderCode(), deviceChannels.getChannelCode());
                                continue;
                            }
                            SaveOrUpdateChannelDTO channelDTO = new SaveOrUpdateChannelDTO();
                            BeanUtils.copyProperties(deviceChannels, channelDTO);
                            channelDTO.setDeviceCode(device.getDeviceCode());
                            channelDTO.setChannelStatus(deviceChannels.getStatus());
                            channelDTOList.add(channelDTO);
                            channelCodeList.add(deviceChannels.getChannelCode());
                        }
                    }
                }
            }
        }
        orderGateway.setAddRecordDeviceMap(addDeviceMap);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(deviceDTOList)) {
            orderVideoDeviceService.batchSaveOrUpdateRecorderDevice(deviceDTOList);
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(channelDTOList)) {
            orderVideoDeviceService.batchSaveOrUpdateRecorderChannel(channelDTOList);
        }
    }
}
