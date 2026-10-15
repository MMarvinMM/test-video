package com.chinatower.sync.consumer.dao;

import com.chinatower.device.entity.vo.UavNestLocationVO;
import com.chinatower.sync.consumer.item.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 喇叭管理
 */
@Mapper
public interface UavDeviceDAO {

    List<DeviceAppModVO> queryDeviceAppModInfo(@Param("deviceCodeList") List<String> deviceCodeList);

    int insertDeviceBatch(@Param("list") List<UavDeviceReport> list);

    int updateDeviceBatch(@Param("list") List<UavDeviceReport> list);

    int updateDeviceModBatch(@Param("list") List<UavDeviceReport> list);

    List<Long> queryUavMountYgIdList(@Param("ygIdList") List<Long> ygIdList);

    int insertMountModelBatch(@Param("list") List<UavMountModelReport> list);

    int updateMountModelBatch(@Param("list") List<UavMountModelReport> list);

    List<String> queryUavCameraModelCodeList(@Param("modelCodeList") List<String> modelCodeList);

    void insertCameraModelBatch(@Param("list") List<UavCameraModelReport> insertList);

    void updateCameraModelBatch(@Param("list") List<UavCameraModelReport> updateList);

    List<String> queryUavCameraParamCodeList(@Param("modelCodeList") List<String> modelCodeList);

    void insertCameraParamBatch(@Param("list") List<UavCameraParamReport> insertList);

    void updateCameraParamBatch(@Param("list") List<UavCameraParamReport> updateList);

    List<String> queryExistUavYgIdList(@Param("ygIdList") List<String> modelCodeList);

    int insertUavModelBatch(@Param("list") List<UavModelReport> list);

    int updateUavModelBatch(@Param("list") List<UavModelReport> list);

    int deleteUavModelBatch(@Param("ygIdList") List<String> list);

    List<String> getAllUavModelYgId();

    int updateDeviceStatusBatch(@Param("list") List<UavDeviceStatusReport> list);

    List<Long> queryUavCameraModelYgIdList(@Param("modelIdList") List<Long> modelIdList);

    void deleteUavCameraParamById(@Param("ids") List<Long> ids);

    List<Long> queryUavCameraParamYgIdList(@Param("idList")List<Long> idList);

    /**
     * 修改无人机经纬度位置信息
     */
    int updateUavDeviceLocationInfo(@Param("locationInfoList")List<UavNestLocationVO> locationInfoList);

    List<UavDeviceReport> queryBindNestCodeByUavDeviceCode(@Param("deviceCodes") List<String> deviceCodes);


    List<Long> queryUavMountRelYgIdList(@Param("ygIdList") List<Long> ygIdList);

    int insertMountModelRelBatch(@Param("list") List<UavMountModelRelReport> list);


    int updateMountModelRelBatch(@Param("list") List<UavMountModelRelReport> list);
}
