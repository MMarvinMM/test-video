package com.chinatower.system.api.feign.factory;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.system.api.feign.client.WangGuanFeignClient;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * @Author：liuxt
 * @Description：网关服务降级处理
 * @Date：2022/8/5 11:34
 * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
 */
@Component
public class WangGuanrFallbackFactory implements FallbackFactory<WangGuanFeignClient>
{
    private static final Logger log = LoggerFactory.getLogger(WangGuanrFallbackFactory.class);

    @Override
    public WangGuanFeignClient create(Throwable throwable)
    {
        log.error("网关服务调用失败:{}", throwable.getMessage());
        return new WangGuanFeignClient()
        {
            @Override
            public Result appSignAuth(JSONObject jsonObject)
            {
                return Result.fail("行业版本信息、订阅者API信息推送失败");
            }

        };
    }
}
