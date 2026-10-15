package com.chinatower.order.service;


import com.chinatower.device.entity.dto.IotDeviceMsgDTO;
import com.chinatower.order.domain.HornDeviceDO;
import com.chinatower.platform.entity.device.SaveOrUpdateChannelDTO;
import com.chinatower.platform.entity.device.SaveOrUpdateDeviceDTO;

import java.util.List;

/**
 * @ClassName IOrderVideoDeviceService
 * @Deacription 设备服务类
 * （为保证订购事务一致性，相关接口在order工程重写一份，后续逻辑有改动需要同步修改）
 * @Author chenmf
 * @Date 2022/6/10 16:17
 * @Version 1.0
 **/
public interface IOrderVideoDeviceService {
    
    /**
     * 批量保存或更新设备信息
     * @param: deviceDTOList
     * @return void
     * @author chenmf
     * @date 2022/6/10
     * @version
     */
    void batchSaveOrUpdateDevice(List<SaveOrUpdateDeviceDTO> deviceDTOList);

    /**
     * 是否私有化
     * @return true - 私有化 false - 否
     */
    boolean isDependenceFlag();

    /**
     * 批量保存或更新物联设备信息
     * @param: deviceDTOList
     * @return void
     * @author chenmf
     * @date 2022/6/10
     * @version
     */
    void batchSaveOrUpdateIotDevice(List<IotDeviceMsgDTO> deviceDTOList);

    /**
     * 批量保存或更新喇叭设备信息
     * @param deviceList 设备信息
     */
    void batchSaveOrUpdateHornDevice(List<HornDeviceDO> deviceList);
    
    /**
     * 批量保存或更新设备通道信息
     * @param: channelDTOList
     * @return void
     * @author chenmf
     * @date 2022/6/10
     * @version
     */
    void batchSaveOrUpdateChannel(List<SaveOrUpdateChannelDTO> channelDTOList);

    /**
     * 批量保存无人机设备信息
     * @param deviceDTOList 无人机设备信息
     */
    void batchSaveOrUpdateUavDevice(List<SaveOrUpdateDeviceDTO> deviceDTOList);

    /**
     * 批量保存无人机机巢设备信息
     * @param deviceDTOList 无人机设备信息
     */
    void batchSaveOrUpdateUavNestDevice(List<SaveOrUpdateDeviceDTO> deviceDTOList);

    /**
     * 批量保存执法记录仪设备信息
     * @param deviceDTOList 执法记录仪设备信息
     */
    void batchSaveOrUpdateRecorderDevice(List<SaveOrUpdateDeviceDTO> deviceDTOList);

    /**
     * 批量保存执法记录仪设备通道信息
     * @param channelDTOList 执法记录仪设备通道信息
     */
    void batchSaveOrUpdateRecorderChannel(List<SaveOrUpdateChannelDTO> channelDTOList);
}
