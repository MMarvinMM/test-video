package com.chinatower.device.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.device.api.IAirConfigService;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.FlightRadiusVo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


/**
 * 降级处理
 */
@Component
public class AirConfigFallbackFactory implements FallbackFactory<IAirConfigService> {

    @Override
    public IAirConfigService create(Throwable throwable) {
        return new IAirConfigService() {
            @Override
            public Result getUavPwdConfig(AirPwdReq airPwdReq) {
                return Result.fail("获取无人机密码配置失败:" + throwable.getMessage());
            }

            @Override
            public Result checkUavPwd(AirPwdReq airPwdReq) {
                return Result.fail("校验无人机密码失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<FlightRadiusVo>> queryFlightRadiusByUavCode(List<String> uavCodeList) {
                return Result.fail("通过无人机编码查询可飞行半径距离失败:" + throwable.getMessage());
            }
        };
    }
}
