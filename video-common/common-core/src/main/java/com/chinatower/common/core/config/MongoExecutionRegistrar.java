package com.chinatower.common.core.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
public class MongoExecutionRegistrar implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{
                MongoLoggerAspect.class.getName()
        };
    }

    @Aspect
    public static class MongoLoggerAspect {

        public MongoLoggerAspect() {
            log.info("MongoLoggerAspect init ...");
        }

        /**
         * 切面逻辑，无需实现其他代码
         */
        @Pointcut("execution(public * org.springframework.data.mongodb.core.MongoTemplate.*(..))" )
        public void point() {
            // Do nothing because of 切面逻辑，无需实现其他代码.
        }

        /**
         * 切面定义
         * @param joinPoint
         * @return
         * @throws Throwable
         */
        @Around("point()")
        public Object before(ProceedingJoinPoint joinPoint) throws Throwable {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            Signature signature = joinPoint.getSignature();
            Object result =  joinPoint.proceed(joinPoint.getArgs());
            try {
                stopWatch.stop();
                log.info("mongo method = {} args = {} execTime = {}",
                        signature, JSON.toJSONString(Arrays.asList(joinPoint.getArgs())), stopWatch.getTime());
            } catch (Throwable e) {
                log.error("日志切面异常", e);
            }

            return result;
        }
    }
}
