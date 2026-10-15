package com.chinatower.system.api.feign.factory;


import com.chinatower.common.entity.Result;
import com.chinatower.platform.model.dto.AuthConfig;
import com.chinatower.system.api.feign.client.WxFeignClient;
import com.chinatower.system.api.model.WxLogoutVo;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WxFallbackFactory implements FallbackFactory<WxFeignClient> {


    @Override
    public WxFeignClient create(Throwable throwable) {
        log.error("调用video-auth服务失败:{}", throwable.getMessage());
        return new WxFeignClient() {
            @Override
            public Result invalidUser(WxLogoutVo vo) {
                log.error("发送重新登录的微信提示失败");
                return Result.fail("发送重新登录的微信提示失败");
            }

            @Override
            public Result<AuthConfig> getConfig() {
                log.error("获取登录配置失败");
                return Result.fail("获取登录配置失败");
            }
        };
  }
}