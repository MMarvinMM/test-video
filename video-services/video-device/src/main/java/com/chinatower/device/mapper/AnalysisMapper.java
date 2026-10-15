package com.chinatower.device.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.device.dto.AnalysisDTO;
import com.chinatower.device.entity.dto.IndTenantDeviceDTO;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.admin.SysArea;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 功能描述: 自定义的 统计数据 DAO层
 *
 * @ClassName AnalysisMapper
 * @auther: chenshaowu
 * @date: 2022/2/16
 * @version: 1.0
 */
@Mapper
public interface AnalysisMapper {

    @DS("statistic")
    Map getStatisticsDevices(@Param("param") AnalysisDTO analysisDTO);
    @DS("base")
    Map getStatisticsDevicesUser(@Param("param") AnalysisDTO analysisDTO);
    @DS("base")
    Map getStatisticsChannelUser(@Param("param") AnalysisDTO analysisDTO);
    @DS("base")
    @DataScope(tenantColumn = "dataTable.tenant_id",deviceColumn = "dataTable.device_code",industryCode = "dataTable.industry_code",acceptColumn="dataTable.accept_node")
    List<Map<String, Integer>> getStatisticsDevicesUserInd(IndTenantDeviceDTO indTenantDeviceDTO);
    @DS("base")
    @DataScope(tenantColumn = "dataTable.tenant_id",deviceColumn = "dataTable.device_code",industryCode = "dataTable.industry_code",acceptColumn="dataTable.accept_node")
    List<Map<String, Integer>> getStatisticsChannelUserInd(IndTenantDeviceDTO indTenantDeviceDTO);
    @DS("base")
    List<SysArea> getSysAreas(@Param("areaCodes") ArrayList<String> areaCodes, @Param("level") Integer level);

}
