package com.chinatower.device.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.device.api.IWarningEventService;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.DeviceErrorVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @ClassName WarningEventFallbackFactory
 * @Description 降级处理
 * @Author chenshaowu
 * @Date 2022/3/11
 **/
@Component
public class WarningEventFallbackFactory implements FallbackFactory<IWarningEventService> {
    @Override
    public IWarningEventService create(Throwable throwable) {
        return new IWarningEventService() {
            @Override
            public Result querySitesForWE(SiteRequestDTO siteRequestDTO) {
                return Result.fail("预警事件-查询站址信息失败:" + throwable.getMessage());
            }

            @Override
            public Result insertDeviceError(DeviceErrorVO deviceErrorVO) {
                return Result.fail("5.3.故障告警消息报文:" + throwable.getMessage());
            }
        };
    }
}
