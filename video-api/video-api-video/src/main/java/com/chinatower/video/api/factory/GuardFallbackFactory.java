package com.chinatower.video.api.factory;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.video.api.IGuardService;
import com.chinatower.video.api.entity.GuardDTO;
import com.chinatower.video.api.entity.VideoRequestDTO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @ClassName GuardFallbackFactory
 * @Description
 * @Author chenshaowu
 * @Date 2022/2/21
 **/
@Component
public class GuardFallbackFactory implements FallbackFactory<IGuardService> {
    @Override
    public IGuardService create(Throwable throwable) {
        return new IGuardService() {
            @Override
            public Result doSetGuard(GuardDTO guardDTO) {
                return Result.fail("调用设置看守卫方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<JSONObject> getAlarmInfo(VideoRequestDTO videoRequestDTO) {
                return Result.fail("调用查询事件告警详情方法失败:" + throwable.getMessage());
            }
        };
    }
}
