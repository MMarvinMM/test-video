package com.chinatower.platform.consumer.dao;

import com.chinatower.platform.consumer.item.*;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 执法记录仪设备信息DAO
 */
@Mapper
public interface RecorderDeviceDAO {

    /**
     * 批量修改执法记录仪设备信息
     * @param list
     * @return
     */
    int updateDeviceBatch(@Param("list") List<RecorderDeviceReport> list);

    /**
     * 根据设备查询应用修改标识
     * @param deviceCodeList
     * @return
     */
    List<DeviceAppModVO> queryDeviceAppModInfo(@Param("deviceCodeList") List<String> deviceCodeList);

    /**
     * 批量修改执法记录仪设备信息
     * @param list
     * @return
     */
    int updateDeviceModBatch(@Param("list") List<RecorderDeviceReport> list);

    /**
     * 批量新增执法记录仪设备信息
     * @param list
     * @return
     */
    int insertDeviceBatch(@Param("list") List<RecorderDeviceReport> list);

    /**
     * 批量修改执法记录仪通道信息
     * @param list
     * @return
     */
    int updateDeviceChannelBatch(@Param("list") List<RecorderChannelReport> list);

    /**
     * 查询执法记录仪通道编号
     * @param channelCodeList
     * @return
     */
    List<String> queryExistChannelCodeList(@Param("channelCodeList") List<String> channelCodeList);

    /**
     * 批量新增执法记录仪通道信息
     * @param list
     * @return
     */
    int insertDeviceChannelBatch(@Param("list") List<RecorderChannelReport> list);
}
