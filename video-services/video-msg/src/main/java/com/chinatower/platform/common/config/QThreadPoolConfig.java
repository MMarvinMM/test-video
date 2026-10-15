package com.chinatower.platform.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @description:
 * @create: 2020/9/4
 **/
@Configuration
public class QThreadPoolConfig {

    // 核心线程池大小
    private int corePoolSize = 10;

    // 最大可创建的线程数
    private int maxPoolSize = 20;

    // 队列最大长度
    private int queueCapacity = 2000;

    // 线程池维护线程所允许的空闲时间
    private int keepAliveSeconds = 60;

    /**
     *  mq消费线程池
     * @return Executor
     */
    @Bean(name="msgTaskExecutor")
    public ThreadPoolTaskExecutor msgTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程数目
        executor.setCorePoolSize(corePoolSize);
        //指定最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        //队列中最大的数目
        executor.setQueueCapacity(queueCapacity);
        //线程名称前缀
        executor.setThreadNamePrefix("msgTaskExecutor_");
        //rejection-policy：当pool已经达到max size的时候，如何处理新任务
        //CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
        //对拒绝task的处理策略
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        //线程空闲后的最大存活时间
        executor.setKeepAliveSeconds(keepAliveSeconds);
        //加载
        executor.initialize();
        return executor;
    }

}
