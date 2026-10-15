package com.chinatower.common.core.config;

import cn.hutool.core.date.StopWatch;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Component
public class TraceInteceptor implements MethodInterceptor {

    @Resource
    private HttpServletRequest request;

    @PostConstruct
    public void init() {
        log.info("inteceptor 启动成功");
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = null;
        try {
            String name;
            Object[] arguments;
            Object aThis;
            try {
                String traceId = request.getHeader("X-TraceId");
                if (traceId != null) {
                    MDC.put("X", traceId);
                } else {
                    MDC.put("X", UUID.randomUUID().toString());
                }
                log.info("开始记录日志");
                name = invocation.getMethod().getName();
                arguments = invocation.getArguments();
                aThis = invocation.getThis();
                log.info("input -> \n 目标类 = {} \n 参数 = {} \n 目标方法 = {}",
                        aThis.getClass().getSimpleName(), Arrays.toString(arguments), name);

                stopWatch.stop();
            } catch (Throwable e) {
                throw new TraceException(e);
            }

            result = invocation.proceed();

            log.info("input -> \n 目标类 = {} \n 参数 = {} \n 目标方法 = {} \n 结果 = {} \n 耗时 = {} ms",
                    aThis.getClass().getSimpleName(), Arrays.toString(arguments),
                    name, JSON.toJSONString(result), stopWatch.getTotalTimeMillis());
            return result;
        } catch (TraceException e) {
            log.error("MDC 配置异常");
            return result;
        } finally {
            MDC.clear();
        }
    }

    static class TraceException extends RuntimeException {
        public TraceException(Throwable cause) {
            super(cause);
        }
    }
}
