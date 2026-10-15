package com.chinatower.analysis.modules.lowalt.service;


import com.chinatower.analysis.api.lowalt.domain.DeviceStatsDTO;
import com.chinatower.analysis.api.lowalt.domain.vo.DeviceDistributionVO;
import com.chinatower.analysis.api.lowalt.domain.vo.DeviceOnlineRateVO;
import com.chinatower.analysis.api.lowalt.domain.vo.DeviceStatsVO;

import java.util.List;

/**
 * 低空设备-统计服务
 */
public interface LowaltDeviceStatsService {

    /**
     * 获取设备数量统计
     */
    List<DeviceStatsVO> deviceStatistics(DeviceStatsDTO deviceStatsDTO);

    /**
     * 获取设备分布Top5
     */
    List<DeviceDistributionVO> getDeviceDistributionTop5(DeviceStatsDTO deviceStatsDTO);

    /**
     * 获取设备在线率统计
     */
    List<DeviceOnlineRateVO> getDeviceOnlineRate(DeviceStatsDTO deviceStatsDTO);


}