package com.chinatower.sync.consumer.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.sync.consumer.item.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName SensorStatusDAO
 * @Deacription
 * @Author zhujun
 * @Date 2022/3/31
 * @Version 1.0
 **/
@Mapper
public interface SensorStatusDAO {

    List<String> queryDevice(@Param("list") List<String> deviceCodes);

    List<DeviceAppModVO> queryDeviceAppModInfo(@Param("deviceCodeList") List<String> deviceCodeList);

    List<DeviceAppModVO> queryDeviceAppModAndVersionInfo(@Param("deviceCodeList") List<String> deviceCodeList);

    int addDevice(@Param("item") SensorStatusReport sensorStatusReport);

    int insertDeviceBatch(@Param("list") List<SensorStatusReport> list);

    int insertDeviceBatch4Aiot(@Param("list") List<SensorStatusReport> list);

    int updateDevice(@Param("item") SensorStatusReport sensorStatusReport);

    int updateDeviceBatch(@Param("list") List<SensorStatusReport> list);

    int updateDeviceBatch4Aiot(@Param("list") List<SensorStatusReport> list);

    int updateDeviceStatusBatch4Aiot(@Param("list") List<SensorStatusReport> list);

    int updateDeviceModBatch(@Param("list") List<SensorStatusReport> list);

    int updateDeviceModBatch4Aiot(@Param("list") List<SensorStatusReport> list);

    int queryMonitor(@Param("deviceCode") String deviceCode, @Param("monitorName") String monitorName);

    List<MonitorStatusReprot> queryMonitorList(@Param("deviceCodeList") List<String> deviceCodeList,
                                               @Param("monitorNameList") List<String> monitorNameList);

    int insertMonitorBatch(@Param("list") List<MonitorStatusReprot> list);

    int modifyMonitorBatch(@Param("list") List<MonitorStatusReprot> list);

    int updateMonitorBatch(@Param("list") List<MonitorStatusReprot> list);

    /**
     * 查询设备型号数量
     *
     * @param modelCode   厂家编码
     * @param deviceModel 设备型号
     * @return 数量
     */
    int countIotDeviceModel(@Param("modelCode") String modelCode, @Param("deviceModel") String deviceModel);

    List<SensorStatusReport> queryIotDeviceModel(@Param("modelCodeList") List<String> modelCodeList,
                                                 @Param("deviceModelList") List<String> deviceModelList);

    /**
     * 新增设备型号信息
     *
     * @param modelCode   厂家编码
     * @param deviceModel 设备型号
     * @param deviceType  设备类型
     * @return 影响条数
     */
    int addIotDeviceModel(@Param("modelCode") String modelCode, @Param("deviceModel") String deviceModel,
                          @Param("deviceType") String deviceType);

    int addIotDeviceModelBatch(@Param("list") List<SensorStatusReport> list);

    /**
     * 查询是否为属地化版本
     */
    SysConfig getSysConfig(String configKey);

    int updateLocationDeviceBatch(@Param("list") List<LocalizationSensorStatusReport> sensorStatusReports);

    int updateRadarDeviceBatch(@Param("list") List<RadarStatusReport> radarStatusReportList);

    int updateHornDevice(@Param("param") HornStatusReport hornStatusReport);

    int updateHornDeviceBatch(@Param("list") List<HornStatusReport> list);

    /**
     * 通过传感器设备编码获取租户
     * @param deviceCodeList
     * @return
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map<String, Object>> getCustomersBySensorDeviceList(@Param("list") List<String> deviceCodeList);

    /**
     * 根据条件查询租户下物联告警配置信息
     * @param param
     * @return
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    Integer queryDeviceSensorMonitorCount(@Param("param") Map<String, Object> param);
}
