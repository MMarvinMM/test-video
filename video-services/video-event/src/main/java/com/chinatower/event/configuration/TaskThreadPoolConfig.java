package com.chinatower.event.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description 线程池配置
 * @Author guyuefei
 * @Date 2022/7/20
 * @Version release_v1.5
 **/
@ConfigurationProperties(prefix = "task.pool")
@Data
public class TaskThreadPoolConfig {

    /** 核心线程数 **/
    private int corePoolSize;
    /** 最大线程数 **/
    private int maxPoolSize;
    /** 保持活跃时间 **/
    private int keepAliveSeconds;
    /** 队列大小 **/
    private int queueCapacity;

}
