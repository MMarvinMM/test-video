package com.chinatower.sync.consumer.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.sync.consumer.entity.*;

import java.util.List;

/**
 * <p>
 * 算法配置 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-19
 */
public interface AlgorithmConfigMapper extends BaseMapper<AlgorithmConfigDO> {

    List<TaskIdAndStatusDto> getAlgorithmStatusByTaskIds(List<TaskIdAndStatusDto> list);
    void updateAlgorithmStatusBatch(List<TaskIdAndStatusDto> list);
    void insertSelectDeviceInfoByDevice(List<String> list);
    void deleteDeviceInfoByDevice(List<String> list);
    void insertSelectChannelInfoByDevice(List<String> list);
    void deleteChannelInfoByDevice(List<String> list);
    List<String> getChannelCodesByDevices(List<String> list);
    void insertSelectChannelInfoByChannel(List<String> list);
    void deleteChannelInfoByChannel(List<String> list);
    List<String> queryModFlagByList(List<DeviceInfoDTO> deviceInfoDTOList);
    List<DeviceCodeAndLonAndLatDto> getDeviceCodeByCodes(List<DeviceInfoDTO> batchUpdateDeviceList);
    void updateDeviceInfoBatch(List<DeviceInfoDTO> list);
    void insertDeviceInfoBatch(List<DeviceInfoDTO> list);
    List<String> getChannelCodeByChannelCodes(List<ChannelInfoDTO> list);
    void updateChannelInfoBatch(List<ChannelInfoDTO> list);
    void insertChannelInfoBatch(List<ChannelInfoDTO> updateChannelInfoList);

    List<DeviceInfoDTO> selectByDeviceCodes(List<String> list);

    void insertDeviceInfoDelBatch(List<DeviceInfoDTO> list);

    List<ChannelInfoDTO> selectChannelByDeviceCodes(List<String> list);

    void insertChannelInfoDelBatch(List<ChannelInfoDTO> list);

    List<ChannelInfoDTO> selectChannelInfoByChannel(List<String> list);

    void deleteTenantDeviceChannelByDevice(List<String> list);

    void deleteTenantDeviceChannelByChannel(List<String> list);
}
