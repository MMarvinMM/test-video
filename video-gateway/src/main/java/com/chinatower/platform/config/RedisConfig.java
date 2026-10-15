package com.chinatower.platform.config;

import com.chinatower.redis.config.TowerRedisAutoConfiguration;
import com.chinatower.redis.props.TowerMultiRedisProperties;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

import java.time.Duration;

/**
 * @Description:  redis配置连接池
 * @Author: 张帅
 * @Date: 2021/11/5 16:10
 **/
@Configuration
public class RedisConfig {

    @Autowired
    private TowerRedisAutoConfiguration towerRedisAutoConfiguration;

    @Bean
    LettuceConnectionFactory lettuceConnectionFactory() {
        TowerMultiRedisProperties towerMultiRedisProperties = towerRedisAutoConfiguration.getPoolConfig();
        // 连接池配置
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(towerMultiRedisProperties.getMaxPoolIdex());
        poolConfig.setMinIdle(towerMultiRedisProperties.getMinPoolIdle());
        poolConfig.setMaxTotal(towerMultiRedisProperties.getMaxPoolTotal());
        poolConfig.setMaxWaitMillis(towerMultiRedisProperties.getMaxWaitMillis());

        //方案二
        LettucePoolingClientConfiguration lettucePoolingClientConfiguration = LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig).commandTimeout(Duration.ofMillis(towerMultiRedisProperties.getCommandTimeout()))
                .clientOptions(io.lettuce.core.cluster.ClusterClientOptions.builder()
                        .validateClusterNodeMembership(false)
                        .build())
                .build();

        //方案一
//        LettucePoolingClientConfiguration lettucePoolingClientConfiguration = LettucePoolingClientConfiguration.builder()
//                .poolConfig(poolConfig).commandTimeout(Duration.ofMillis(towerMultiRedisProperties.getCommandTimeout()))
//                .clientOptions(io.lettuce.core.cluster.ClusterClientOptions.builder()
//                        .topologyRefreshOptions(io.lettuce.core.cluster.ClusterTopologyRefreshOptions.builder()
//                                .enablePeriodicRefresh(Duration.ofMillis(1000))
//                                .enableAllAdaptiveRefreshTriggers()
//                                .build())
//                        .build())
//                .build();

        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(towerMultiRedisProperties.getPrimary().getHost());
        redisConfig.setPort(towerMultiRedisProperties.getPrimary().getPort());
        redisConfig.setPassword(towerMultiRedisProperties.getPrimary().getPassword());
        return new LettuceConnectionFactory(redisConfig, lettucePoolingClientConfiguration);
    }

}
