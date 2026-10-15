package com.chinatower.message.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.message.api.LowAltMsgAPI;
import com.chinatower.message.api.model.LowAltPointMsg;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 消息服务降级处理
 *
 * @author chinatower
 */
@Component
public class LowAltMsgFallbackFactory implements FallbackFactory<LowAltMsgAPI> {
    private static final Logger log = LoggerFactory.getLogger(LowAltMsgFallbackFactory.class);


    @Override
    public LowAltMsgAPI create(Throwable throwable) {
        log.error("消息服务调用失败:{}", throwable.getMessage());
        return new LowAltMsgAPI() {

            @Override
            public Result<String> lowAltPointNotice(LowAltPointMsg lowAltPointMsg) {
                return Result.fail("消息服务调用失败");
            }
        };
    }
}
