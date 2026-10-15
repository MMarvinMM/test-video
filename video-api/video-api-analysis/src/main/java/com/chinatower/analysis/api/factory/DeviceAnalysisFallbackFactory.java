package com.chinatower.analysis.api.factory;

import com.chinatower.analysis.api.AnalysisAPI;
import com.chinatower.analysis.api.DeviceAnalysisAPI;
import com.chinatower.analysis.api.entity.*;
import com.chinatower.analysis.api.entity.dto.IndEventTrendAnalysisDTO;
import com.chinatower.analysis.api.entity.dto.IndStatisticAnalysisDTO;
import com.chinatower.analysis.api.entity.dto.IndTenantDeviceAnalysisDTO;
import com.chinatower.common.entity.Result;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @className: DeviceAnalysisFallbackFactory
 * @description:
 * @author: yongqi.han
 * @date: 2022/8/18
 * @requestID 需求单号及需求名称
 */
@Component
public class DeviceAnalysisFallbackFactory  implements FallbackFactory<DeviceAnalysisAPI> {
    @Override
    public DeviceAnalysisAPI create(Throwable throwable) {
        return new DeviceAnalysisAPI(){

            @Override
            public Result<IndDeviceAnalysisVO> queryDeviceStatistics(IndTenantDeviceAnalysisDTO indTenantDeviceDTO) {
                return Result.fail("调用统计监控设备数量失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<IndDevTypeAnalysisVO>> getDevTypeYearMonth(IndStatisticAnalysisDTO indStatisticDTO) {
                return Result.fail("调用告警类型统计失败:" + throwable.getMessage());
            }

            @Override
            public Result<IndEventAlarmNumAnalysisVO> getEventAlarmNumYearMonth(IndStatisticAnalysisDTO indStatisticDTO) {
                return Result.fail("调用告警来源统计失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<IndEventNumAnalysisVO>> getEventNumByArea(IndStatisticAnalysisDTO indStatisticDTO) {
                return Result.fail("调用告警高频区域统计失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<IndEventTrendAnalysisVO>> getEventTrend(IndEventTrendAnalysisDTO indEventTrendDTO) {
                return Result.fail("调用告警趋势统计失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<Map>> getEventNumByHour(IndAnalysisAnalysisVO indAnalysisVO) {
                return Result.fail("调用告警高频时段统计失败:" + throwable.getMessage());
            }

        };
    }
}
