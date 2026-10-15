package com.chinatower.platform.fegin.factory;

import com.chinatower.platform.fegin.client.MsgFeignClient;
import com.chinatower.platform.fegin.fallback.MsgFeignClientFallback;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 异常处理工厂类    获取到下游异常信息 将异常信息放到回fallback中的cause中
 *
 * @author zs
 * @crate 2019-08-13
 */
@Slf4j
@Component
public class MsgFeignClientFallbackFactory implements FallbackFactory<MsgFeignClient> {
    private static final Logger logger = LoggerFactory.getLogger(MsgFeignClientFallbackFactory.class);


    @Override
    public MsgFeignClient create(Throwable throwable) {
        logger.info("feigin调用异常信息是:{}", throwable.getMessage());
        MsgFeignClientFallback msgFeignClientFallback = new MsgFeignClientFallback();
        msgFeignClientFallback.setCause(throwable);
        return msgFeignClientFallback;
    }
}
