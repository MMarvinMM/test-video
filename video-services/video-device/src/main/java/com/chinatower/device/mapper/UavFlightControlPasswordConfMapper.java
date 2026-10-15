package com.chinatower.device.mapper;

import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.device.entity.dto.VmUavFlightControlPasswordConfDTO;
import com.chinatower.platform.annotation.DataScope;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 无人机飞行控制密码配置mapper
 * @author penghaibo
 */
@Mapper
public interface UavFlightControlPasswordConfMapper {

    @DataScope(tenantColumn = "fcpc.tenant_id", industryCode = "fcpc.industry_code", deviceColumn = "fcpc.device_code")
    List<VmUavFlightControlPasswordConfDTO> queryUavFlightControlPasswordConf(@Param("param") UavDeviceDTO param);

    /**
     * 批量删除飞行控制密码配置
     * @param VmUavFlightControlPasswordConfDTO
     * @return
     */
    @DataScope(tenantColumn = "fcpc.tenant_id", industryCode = "fcpc.industry_code", deviceColumn = "fcpc.device_code")
    int batchDeleteFlightControlPasswordConf(VmUavFlightControlPasswordConfDTO VmUavFlightControlPasswordConfDTO);

    /**
     * 批量新增飞行控制密码配置
     *
     * @param list 飞行控制密码配置列表
     * @return 新增成功的记录数
     */
    int batchInsertFlightControlPasswordConf(@Param("list") List<VmUavFlightControlPasswordConfDTO> list);

}