package com.chinatower.sync.consumer.dao;

import com.chinatower.sync.consumer.item.DeviceAppModVO;
import com.chinatower.sync.consumer.item.RecorderChannelReport;
import com.chinatower.sync.consumer.item.RecorderDeviceReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecorderDeviceDAO {

    int updateDeviceBatch(@Param("list") List<RecorderDeviceReport> list);

    List<DeviceAppModVO> queryDeviceAppModInfo(@Param("deviceCodeList") List<String> deviceCodeList);

    int updateDeviceModBatch(@Param("list") List<RecorderDeviceReport> list);

    int insertDeviceBatch(@Param("list") List<RecorderDeviceReport> list);

    int updateDeviceChannelBatch(@Param("list") List<RecorderChannelReport> list);

    List<String> queryExistChannelCodeList(@Param("channelCodeList") List<String> channelCodeList);

    int insertDeviceChannelBatch(@Param("list") List<RecorderChannelReport> list);
}
