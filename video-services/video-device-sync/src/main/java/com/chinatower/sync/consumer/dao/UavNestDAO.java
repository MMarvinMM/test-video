package com.chinatower.sync.consumer.dao;

import com.chinatower.device.entity.vo.UavNestLocationVO;
import com.chinatower.sync.consumer.entity.ChannelStatusReq;
import com.chinatower.sync.consumer.item.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UavNestDAO {

    int insertDeviceBatch(@Param("list") List<UavNestDeviceReport> list);

    int updateDeviceBatch(@Param("list") List<UavNestDeviceReport> list);

    int updateDeviceModBatch(@Param("list") List<UavNestDeviceReport> list);

    int insertDeviceChannelBatch(@Param("list") List<UavNestChannelReport> list);

    int updateDeviceChannelBatch(@Param("list") List<UavNestChannelReport> list);

    List<DeviceAppModVO> queryDeviceAppModInfo(@Param("deviceCodeList") List<String> deviceCodeList);

    List<String> queryExistChannelCodeList(@Param("channelCodeList") List<String> channelCodeList);

    List<String> queryExistUavNestYgIdList(@Param("ygIdList") List<String> modelCodeList);

    int insertUavNestModelBatch(@Param("list") List<UavNestModelReport> list);

    int updateUavNestModelBatch(@Param("list") List<UavNestModelReport> list);

    int deleteUavNestModelBatch(@Param("ygIdList") List<String> list);

    List<String> getAllUavNestModelYgId();

    int updateDeviceStatusBatch(@Param("list") List<UavDeviceStatusReport> list);

    int updateDeviceChannelStatusBatch(@Param("list") List<ChannelStatusReq> list);

    int updateUavNestDeviceLocationInfo(@Param("locationInfoList")List<UavNestLocationVO> locationInfoList);

}
