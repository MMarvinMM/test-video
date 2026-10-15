package com.chinatower.sync.consumer.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.sync.consumer.entity.ChannelInfoDO;
import com.chinatower.sync.consumer.entity.ChannelStatusReq;
import com.chinatower.sync.consumer.entity.DeviceInfoDO;
import com.chinatower.sync.consumer.entity.DeviceStatusReq;
import com.chinatower.sync.consumer.service.IChannelInfoRepository;
import com.chinatower.sync.consumer.service.IDeviceInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * video-device-sync
 * @ClassName DeviceMessageController
 * @Description 设备在离线状态推送(MQ推送）
 * @Author hued
 * @Date 2024年7月11日09:30:17
 */
@RestController
@Slf4j
public class DeviceMessageController {

    @Autowired
    IDeviceInfoRepository deviceInfoRepositoryImpl;

    @Autowired
    IChannelInfoRepository channelInfoRepositoryImpl;

    /**
     *
     * 设备在离线状态推送
     * 6.16 设备在离线状态推送(MQ推送） --废弃，以kafka同步为准
     *
     * @param deviceStatusList
     * @return Result
     * @author zheng.yan
     * @date 2022/1/27
     */
    @PostMapping("/video/message/receiveDeviceChannelStatus")
    public Result<String> receiveDeviceChannelStatus(@RequestBody List<DeviceStatusReq> deviceStatusList) {
        log.info("receiveDeviceChannelStatus message：" + JSON.toJSONString(deviceStatusList));
//        if (deviceStatusList == null || deviceStatusList.isEmpty()) {
//            return Result.success(ResultCode.SUCCESS);
//        }
//        try {
//            List<DeviceInfoDO> deviceInfoList = new ArrayList<>(deviceStatusList.size());
//            List<ChannelInfoDO> channelInfoList = new ArrayList<>(deviceStatusList.size() * 3);
//            // 获取报文中的设备、通道编码
//            List<String> deviceCodes = new ArrayList<>(deviceStatusList.size());
//            List<String> channelCodes = new ArrayList<>(deviceStatusList.size() * 3);
//            for (DeviceStatusReq deviceStatusReq : deviceStatusList) {
//                if (StringUtils.isNotBlank(deviceStatusReq.getDeviceCode())) {
//                    deviceCodes.add(deviceStatusReq.getDeviceCode());
//                }
//                if (deviceStatusReq.getChannelInfos() != null && !deviceStatusReq.getChannelInfos().isEmpty()) {
//                    for (ChannelStatusReq channelStatus : deviceStatusReq.getChannelInfos()) {
//                        if (StringUtils.isBlank(channelStatus.getChannelCode())) {
//                            continue;
//                        }
//                        channelCodes.add(channelStatus.getChannelCode());
//                    }
//                }
//            }
//            List<DeviceInfoDO> sameDeviceInfoList = deviceInfoRepositoryImpl.listDeviceByCodes(deviceCodes);
//            List<ChannelInfoDO> sameChannelInfoList = channelInfoRepositoryImpl.listChannelByCodes(channelCodes);
//
//            for (DeviceStatusReq deviceStatusReq : deviceStatusList) {
//                if (deviceStatusReq.getDeviceStatus() != null) {
//                    DeviceInfoDO sameDevice = this.getSameDeviceByCode(deviceStatusReq.getDeviceCode(), sameDeviceInfoList);
//                    if (sameDevice != null) {
//                        DeviceInfoDO uptDeviceInfo = new DeviceInfoDO();
//                        uptDeviceInfo.setDeviceId(sameDevice.getDeviceId());
//                        uptDeviceInfo.setDeviceStatus(deviceStatusReq.getDeviceStatus());
//                        deviceInfoList.add(uptDeviceInfo);
//                    }
//                }
//                // 通道状态处理
//                if (deviceStatusReq.getChannelInfos() == null || deviceStatusReq.getChannelInfos().isEmpty()) {
//                    continue;
//                }
//                for (ChannelStatusReq channelStatus : deviceStatusReq.getChannelInfos()) {
//                    ChannelInfoDO sameChannel = this.getSameChannelByCode(channelStatus.getChannelCode(), sameChannelInfoList);
//                    if (sameChannel != null) {
//                        ChannelInfoDO channelInfoDO = new ChannelInfoDO();
//                        channelInfoDO.setChannelId(sameChannel.getChannelId());
//                        channelInfoDO.setChannelStatus(channelStatus.getChannelStatus());
//                        channelInfoList.add(channelInfoDO);
//                    }
//                }
//            }
//            if (!deviceInfoList.isEmpty()) {
//                deviceInfoRepositoryImpl.updateBatchById(deviceInfoList);
//            }
//            if (!channelInfoList.isEmpty()) {
//                channelInfoRepositoryImpl.updateBatchById(channelInfoList);
//            }
//        } catch (Exception e) {
//            log.error("receiveDeviceChannelStatus", e);
//            return Result.fail(ResultCode.FAILURE);
//        }
        return Result.success(ResultCode.SUCCESS);
    }

    private DeviceInfoDO getSameDeviceByCode(String deviceCode, List<DeviceInfoDO> deviceInfoList) {
        DeviceInfoDO sameDevice = null;
        if (StringUtils.isNotBlank(deviceCode)) {
            for (DeviceInfoDO deviceInfoDO : deviceInfoList) {
                if (deviceCode.equals(deviceInfoDO.getDeviceCode())) {
                    sameDevice = deviceInfoDO;
                    break;
                }
            }
        }
        return sameDevice;
    }

    private ChannelInfoDO getSameChannelByCode(String channelCode, List<ChannelInfoDO> channelInfoList) {
        ChannelInfoDO sameChannel = null;
        if (StringUtils.isNotBlank(channelCode)) {
            for (ChannelInfoDO channelInfoDO : channelInfoList) {
                if (channelCode.equals(channelInfoDO.getChannelCode())) {
                    sameChannel = channelInfoDO;
                    break;
                }
            }
        }
        return sameChannel;
    }
}
