package com.chinatower.platform.consumer.dao;

import com.chinatower.platform.consumer.item.DeviceAppModVO;
import com.chinatower.platform.consumer.item.DeviceBroadcastReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 喇叭管理
 */
@Mapper
public interface DeviceBroadcastDAO {

    /**
     * 批量新增喇叭设备信息
     * @param list
     * @return
     */
    int insertDeviceBatch(@Param("list") List<DeviceBroadcastReport> list);

    /**
     * 批量修改喇叭设备信息
     * @param list
     * @return
     */
    int updateDeviceBatch(@Param("list") List<DeviceBroadcastReport> list);

    /**
     * 批量修改喇叭设备信息
     * @param list
     * @return
     */
    int updateDeviceModBatch(@Param("list") List<DeviceBroadcastReport> list);

    /**
     * 根据设备编号查询应用修改标识
     * @param deviceCodeList
     * @return
     */
    List<DeviceAppModVO> queryDeviceAppModInfo(@Param("deviceCodeList") List<String> deviceCodeList);
}
