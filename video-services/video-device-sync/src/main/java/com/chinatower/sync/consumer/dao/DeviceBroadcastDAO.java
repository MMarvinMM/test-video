package com.chinatower.sync.consumer.dao;

import com.chinatower.sync.consumer.item.DeviceAppModVO;
import com.chinatower.sync.consumer.item.DeviceBroadcastReport;
import com.chinatower.sync.consumer.item.HornStatusReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 喇叭管理
 */
@Mapper
public interface DeviceBroadcastDAO {

    int insertDeviceBatch(@Param("list") List<DeviceBroadcastReport> list);

    int updateDeviceBatch(@Param("list") List<DeviceBroadcastReport> list);

    int updateDeviceModBatch(@Param("list") List<DeviceBroadcastReport> list);

    List<DeviceAppModVO> queryDeviceAppModInfo(@Param("deviceCodeList") List<String> deviceCodeList);


    int insertDeviceBatchByAIot(@Param("list") List<DeviceBroadcastReport> list);

    int updateDeviceBatchByAIot(@Param("list") List<DeviceBroadcastReport> list);

    int updateDeviceModBatchByAIot(@Param("list") List<DeviceBroadcastReport> list);

    List<DeviceAppModVO> queryDeviceAppModInfoForAIot(@Param("deviceCodeList") List<String> deviceCodeList);

    int updateHornDeviceBatchByAIot(@Param("list") List<HornStatusReport> list);
}
