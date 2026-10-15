package com.chinatower.analysis.api.lowalt;

import com.chinatower.analysis.api.lowalt.domain.DeviceStatsDTO;
import com.chinatower.analysis.api.lowalt.domain.vo.DeviceDistributionVO;
import com.chinatower.analysis.api.lowalt.domain.vo.DeviceOnlineRateVO;
import com.chinatower.analysis.api.lowalt.domain.vo.DeviceStatsVO;
import com.chinatower.analysis.api.lowalt.factory.LowaltDeviceAnalysisFallbackFactory;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "LowaltDeviceAnalysisAPI", value = ServiceNameConstants.STATICS_SERVICE,
        fallbackFactory = LowaltDeviceAnalysisFallbackFactory.class)
public interface LowaltDeviceAnalysisAPI {

    /**
     * 查询低空设备数量统计
     */
    @PostMapping("/lowalt/device/deviceStatistics")
    Result<List<DeviceStatsVO>> deviceStatistics(@RequestBody DeviceStatsDTO deviceStatsDTO);

    /**
     * 查询低空设备分布Top5
     */
    @PostMapping("/lowalt/device/deviceDistributionTop5")
    Result<List<DeviceDistributionVO>> getDeviceDistributionTop5(@RequestBody DeviceStatsDTO deviceStatsDTO);

    /**
     * 查询低空设备在线率统计
     */
    @PostMapping("/lowalt/device/deviceOnlineRate")
    Result<List<DeviceOnlineRateVO>> getDeviceOnlineRate(@RequestBody DeviceStatsDTO deviceStatsDTO);

}
