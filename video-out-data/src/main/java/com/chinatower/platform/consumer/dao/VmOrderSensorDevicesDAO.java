package com.chinatower.platform.consumer.dao;

import com.chinatower.outdata.api.entity.DeviceInfoMongoDO;
import com.chinatower.outdata.api.entity.DeviceModelMongoDO;
import com.chinatower.platform.consumer.item.VmOrderSensorDevicesDO;
import com.chinatower.platform.consumer.item.WarningTargetRadarTemplateDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VmOrderSensorDevicesDAO {

    /**
     * 根据租户和行业编码查询客户订购的设备信息列表
     * @param industryCode
     * @param tenantId
     * @return
     */
    List<VmOrderSensorDevicesDO>  select(@Param("industryCode") String industryCode, @Param("tenantId") String tenantId);

    /**
     * 根据租户和行业编码查询客户订购的设备编号集合
     * @param industryCode
     * @param tenantId
     * @return
     */
    List<String> seleceDeviceCodeListByTenantId(@Param("tenantId") String tenantId,@Param("industryCode") String industryCode);

    /**
     * 根据任务ID查询雷达告警模版信息
     * @param taskIdList
     * @return
     */
    List<WarningTargetRadarTemplateDTO> selectRadarTemplateInfoByTaskIdList(@Param("taskIdList") List<String> taskIdList);

    List<DeviceInfoMongoDO> getDeviceInfoList();

    List<DeviceModelMongoDO> getDeviceModelInfoList();
}
