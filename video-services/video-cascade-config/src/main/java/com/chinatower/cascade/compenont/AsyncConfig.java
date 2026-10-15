package com.chinatower.cascade.compenont;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

/**
 * @Author 蔡舒豪
 * @CreateTime 2021/5/25 9:26
 * @Description 异步事件线程池
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {
    /**
     * 自定义异步线程池，若不重写会使用默认的线程池
     * @return
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        // 核心线程数
        taskExecutor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        // 最大线程数
        taskExecutor.setMaxPoolSize(Runtime.getRuntime().availableProcessors()*2);
        // 队列大小 获取CPU的核数
        taskExecutor.setQueueCapacity(1000);
        // 线程名的前缀
        taskExecutor.setThreadNamePrefix("异步-线程-");
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }
    class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
        @Override
        public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
            throwable.printStackTrace();
            log.error("异步线程异常信息 - {}" , throwable.getMessage());
            log.info("方法名 - {}", method.getName());
            for (Object param : objects) {
                log.info("参数 - {}" , param);
            }
        }
    }
}
