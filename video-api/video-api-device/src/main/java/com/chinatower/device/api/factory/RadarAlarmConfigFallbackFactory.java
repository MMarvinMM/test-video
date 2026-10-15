package com.chinatower.device.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.device.api.IRadarAlarmConfigService;
import com.chinatower.device.entity.dto.RadarAlarmRegionDTO;
import com.chinatower.device.entity.dto.RadarAlarmTemplateDTO;
import com.chinatower.device.entity.vo.RadarAlarmRegionVo;
import com.chinatower.device.entity.vo.RadarAlarmTemplateVO;
import com.github.pagehelper.PageInfo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;


/**
 * 降级处理
 */
@Component
public class RadarAlarmConfigFallbackFactory implements FallbackFactory<IRadarAlarmConfigService> {
    @Override
    public IRadarAlarmConfigService create(Throwable throwable) {
        return new IRadarAlarmConfigService() {

            @Override
            public Result<PageInfo<RadarAlarmRegionVo>> queryRadarAlarmRegion(RadarAlarmRegionDTO dto) {
                return Result.fail("查询雷达告警区域失败" + throwable.getMessage());
            }

            @Override
            public Result<PageInfo<RadarAlarmTemplateVO>> queryRadarAlarmTemplate(RadarAlarmTemplateDTO dto) {
                return Result.fail("查询雷达告警模板失败" + throwable.getMessage());
            }
        };
    }
}
