package com.chinatower.device.mapper;


import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.device.entity.dto.VmUavSecureStrategyDTO;
import com.chinatower.device.entity.dto.VmUavUnconnectedAlarmDTO;
import com.chinatower.platform.annotation.DataScope;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 无人机安全策略mapper
 */
@Mapper
public interface UavSecureStrategyMapper {

    @DataScope(tenantColumn = "ss.tenant_id" ,industryCode = "ss.industry_code", deviceColumn = "ss.device_code")
    List<VmUavSecureStrategyDTO> queryVmUavSecureStrategy(@Param("param") UavDeviceDTO param);

    @DataScope(tenantColumn = "ua.tenant_id" ,industryCode = "ua.industry_code", deviceColumn = "ua.device_code")
    List<VmUavUnconnectedAlarmDTO> queryVmUavUnconnectedAlarm(@Param("param") UavDeviceDTO param);

    /**
     * 批量删除安全策略
     * @param vmUavSecureStrategyDTO
     * @return
     */
    @DataScope(tenantColumn = "ua.tenant_id" ,industryCode = "ua.industry_code", deviceColumn = "ua.device_code")
    int batchDeleteStrategy(VmUavSecureStrategyDTO vmUavSecureStrategyDTO);

    /**
     * 批量删除失联告警
     * @param vmUavUnconnectedAlarmDTO
     * @return
     */
    @DataScope(tenantColumn = "tenant_id" ,industryCode = "industry_code", deviceColumn = "device_code")
    int batchDeleteAlarm(VmUavUnconnectedAlarmDTO vmUavUnconnectedAlarmDTO);

    /**
     * 批量新增安全策略
     * @param list
     * @return
     */
    int batchInsertStrategy(@Param("list") List<VmUavSecureStrategyDTO> list);

    /**
     * 批量新增失联告警
     * @param list
     * @return
     */
    int batchInsertAlarm(@Param("list") List<VmUavUnconnectedAlarmDTO> list);

    /**
     *
     * @param param
     * @return
     */
    List<VmUavSecureStrategyDTO> queryVmUavSecureStrategyByUavTask(UavDeviceDTO param);
}
