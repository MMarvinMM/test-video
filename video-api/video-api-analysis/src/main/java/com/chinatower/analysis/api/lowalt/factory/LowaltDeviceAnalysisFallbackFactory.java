package com.chinatower.analysis.api.lowalt.factory;

import com.chinatower.analysis.api.entity.*;
import com.chinatower.analysis.api.entity.dto.IndTenantDeviceAnalysisDTO;
import com.chinatower.analysis.api.lowalt.LowaltDeviceAnalysisAPI;
import com.chinatower.analysis.api.lowalt.domain.DeviceStatsDTO;
import com.chinatower.analysis.api.lowalt.domain.vo.DeviceDistributionVO;
import com.chinatower.analysis.api.lowalt.domain.vo.DeviceOnlineRateVO;
import com.chinatower.analysis.api.lowalt.domain.vo.DeviceStatsVO;
import com.chinatower.common.entity.Result;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @className: LowaltDeviceAnalysisFallbackFactory
 * @description:
 * @author: hanyq
 * @date: 2025/12/21
 * @requestID 需求单号及需求名称
 */
@Component
public class LowaltDeviceAnalysisFallbackFactory implements FallbackFactory<LowaltDeviceAnalysisAPI> {
    @Override
    public LowaltDeviceAnalysisAPI create(Throwable throwable) {
        return new LowaltDeviceAnalysisAPI(){

            @Override
            public Result<List<DeviceStatsVO>> deviceStatistics(DeviceStatsDTO deviceStatsDTO) {
                return Result.fail("查询低空设备数量统计:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceDistributionVO>> getDeviceDistributionTop5(DeviceStatsDTO deviceStatsDTO) {
                return Result.fail("查询低空设备分布Top5:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceOnlineRateVO>> getDeviceOnlineRate(DeviceStatsDTO deviceStatsDTO) {
                return Result.fail("查询低空设备在线率统计:" + throwable.getMessage());
            }

        };
    }
}
