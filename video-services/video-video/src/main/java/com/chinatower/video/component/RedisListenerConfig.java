package com.chinatower.video.component;

import com.chinatower.redis.props.TowerMultiRedisProperties;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisOperations;

import org.springframework.beans.factory.BeanFactory;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * @Package : com.chinatower.video.component
 * @FileName : RedisListenerConfig
 * @Description : Redis集群监听器配置文件
 * @author : zhaozj
 * @date: 2023/03/25
 * @version : V1.5.75
 */
@Configuration
@EnableConfigurationProperties({TowerMultiRedisProperties.class})
@ConditionalOnProperty(
        value = {"tower.redis.enable"},
        havingValue = "true",
        matchIfMissing = true
)
@ConditionalOnClass({ JedisConnection.class, RedisOperations.class, RedisProperties.Jedis.class, MessageListener.class })
@AutoConfigureAfter({ JacksonAutoConfiguration.class, RedisAutoConfiguration.class })
public class RedisListenerConfig {
    private TowerMultiRedisProperties towerMultiRedisProperties;

    @Bean
    public RedisMessageListenerFactory redisMessageListenerFactory(BeanFactory beanFactory) {
        LettuceConnectionFactory lettuceConnectionFactory = this.createLettuceConnectionFactory(this.towerMultiRedisProperties.getPrimary());
        RedisMessageListenerFactory beans = new RedisMessageListenerFactory();
        beans.setBeanFactory(beanFactory);
        beans.setRedisConnectionFactory(lettuceConnectionFactory);
        return beans;
    }

    public RedisListenerConfig(TowerMultiRedisProperties towerMultiRedisProperties) {
        this.towerMultiRedisProperties = towerMultiRedisProperties;
    }

    public LettuceConnectionFactory createLettuceConnectionFactory(TowerMultiRedisProperties.TowerRedis towerRedis) {
        GenericObjectPoolConfig redisPoolConfig = new GenericObjectPoolConfig();
        redisPoolConfig.setMaxIdle(this.towerMultiRedisProperties.getMaxPoolIdex());
        redisPoolConfig.setMaxTotal(this.towerMultiRedisProperties.getMaxPoolTotal());
        redisPoolConfig.setMinIdle(this.towerMultiRedisProperties.getMinPoolIdle());
        redisPoolConfig.setMaxWaitMillis(10000L);
        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder().commandTimeout(Duration.ofMillis(this.towerMultiRedisProperties.getCommandTimeout()));
        builder.shutdownTimeout(Duration.ofMillis(1000L));
        builder.poolConfig(redisPoolConfig);

        //方案二
        builder.clientOptions(io.lettuce.core.cluster.ClusterClientOptions.builder()
                .validateClusterNodeMembership(false)
                .build());

        //方案一
//        builder.clientOptions(io.lettuce.core.cluster.ClusterClientOptions.builder()
//                        .topologyRefreshOptions(io.lettuce.core.cluster.ClusterTopologyRefreshOptions.builder()
//                                .enablePeriodicRefresh(Duration.ofMillis(1000))
//                                .enableAllAdaptiveRefreshTriggers()
//                                .build())
//                        .build());

        LettuceClientConfiguration lettuceClientConfiguration = builder.build();
        if (towerRedis.isCluster()) {
            RedisClusterConfiguration redisConfig = new RedisClusterConfiguration();
            Set<RedisNode> nodeses = new HashSet();
            String[] hostses = towerRedis.getHost().split(",");
            String[] var8 = hostses;
            int var9 = hostses.length;

            for(int var10 = 0; var10 < var9; ++var10) {
                String h = var8[var10];
                h = h.replace(" ","").replace("\n", "");
                if (!"".equals(h)) {
                    String host = h.split(":")[0];
                    int port = Integer.valueOf(h.split(":")[1]);
                    nodeses.add(new RedisNode(host, port));
                }
            }

            redisConfig.setClusterNodes(nodeses);
            redisConfig.setMaxRedirects(3);
            redisConfig.setPassword(towerRedis.getPassword());
            LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfig, lettuceClientConfiguration);
            lettuceConnectionFactory.afterPropertiesSet();
            return lettuceConnectionFactory;
        } else {
            RedisConfiguration redisConfiguration = new RedisStandaloneConfiguration(towerRedis.getHost(), towerRedis.getPort());
            ((RedisStandaloneConfiguration)redisConfiguration).setDatabase(0);
            ((RedisStandaloneConfiguration)redisConfiguration).setPassword(towerRedis.getPassword());
            LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfiguration, lettuceClientConfiguration);
            lettuceConnectionFactory.afterPropertiesSet();
            return lettuceConnectionFactory;
        }
    }
}
