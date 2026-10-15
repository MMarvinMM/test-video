package com.chinatower.analysis.modules.lowalt.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.analysis.api.lowalt.domain.DeviceStatsDTO;
import com.chinatower.analysis.api.lowalt.domain.vo.DeviceDistributionVO;
import com.chinatower.analysis.api.lowalt.domain.vo.DeviceOnlineRateVO;
import com.chinatower.analysis.api.lowalt.domain.vo.DeviceStatsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 设备统计Mapper
 */
@Mapper
@DS("base")
public interface LowaltDeviceStatisticsMapper {

    /**
     * 查询设备数量统计
     */
    List<DeviceStatsVO> selectDeviceStatistics(DeviceStatsDTO deviceStatsDTO);

    /**
     * 查询设备分布Top5
     */
    List<DeviceDistributionVO> selectDeviceDistributionTop5(DeviceStatsDTO deviceStatsDTO);

    /**
     * 根据区域查询区域下设备类型数量统计
     * @param deviceStatsDTO
     * @return
     */
    List<Map<String,String>> selectDeviceTypeCountTop5(DeviceStatsDTO deviceStatsDTO);

    /**
     * 查询设备在线率
     */
    List<DeviceOnlineRateVO> selectDeviceOnlineRate(DeviceStatsDTO deviceStatsDTO);

    /**
     * 根据区域编码查询区域名称
     * @param areaCodeList
     * @return
     */
    List<Map<String,String>>  getAreaName(@Param("areaCodeList") List<String> areaCodeList);

}