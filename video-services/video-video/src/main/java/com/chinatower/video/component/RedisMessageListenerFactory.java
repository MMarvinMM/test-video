package com.chinatower.video.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import redis.clients.jedis.JedisShardInfo;

import javax.annotation.Resource;

/**
 * @Package : com.chinatower.video.component
 * @FileName : RedisMessageListenerFactory
 * @Description : Redis集群消息监听工厂类
 * @author : zhaozj
 * @date: 2023/03/25
 * @version : V1.5.75
 */
@Slf4j
public class RedisMessageListenerFactory implements BeanFactoryAware, ApplicationListener<ContextRefreshedEvent> {

    @Value("${tower.redis.primary.password}")
    private String password;
    @Value("${tower.redis.primary.cluster}")
    private String cluster;
    @Value("${tower.redis.primary.host}")
    private String host;
    @Value("${tower.redis.primary.port}")
    private int port;

    private DefaultListableBeanFactory beanFactory;

    private RedisConnectionFactory redisConnectionFactory;

    @Resource
    private ApplicationContext applicationContext;

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    public void setRedisConnectionFactory(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if ("false".equals(cluster)) {
            RedisClusterNode node = new RedisClusterNode(host,port);
            handleContainer(node);
        } else {
            RedisClusterConnection redisClusterConnection = redisConnectionFactory.getClusterConnection();
            if (redisClusterConnection != null) {
                Iterable<RedisClusterNode> nodes = redisClusterConnection.clusterGetNodes();
                for (RedisClusterNode node : nodes) {
                    if (node.isMaster()) {
                        handleContainer(node);
                    }
                }
            }
        }
    }


    public void handleContainer(RedisClusterNode node) {
        log.info("获取到redis的master节点为[{}]",node.toString());
        String containerBeanName = "messageContainer" + node.hashCode();
        if (beanFactory.containsBean(containerBeanName)) {
            return;
        }
        JedisShardInfo jedisShardInfo = new JedisShardInfo(node.getHost(), node.getPort());
        jedisShardInfo.setPassword(password);
        JedisConnectionFactory factory = new JedisConnectionFactory(jedisShardInfo);
        BeanDefinitionBuilder containerBeanDefinitionBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(RedisMessageListenerContainer.class);
        containerBeanDefinitionBuilder.addPropertyValue("connectionFactory", factory);
        containerBeanDefinitionBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        containerBeanDefinitionBuilder.setLazyInit(false);
        beanFactory.registerBeanDefinition(containerBeanName,
                containerBeanDefinitionBuilder.getRawBeanDefinition());

        RedisMessageListenerContainer container = beanFactory.getBean(containerBeanName,
                RedisMessageListenerContainer.class);
        String listenerBeanName = "messageListener" + node.hashCode();
        if (beanFactory.containsBean(listenerBeanName)) {
            return;
        }

        MessageListener messageListener = applicationContext.getBean(MessageListener.class);
        log.info("------messageListener------" + messageListener.toString() +"======"+ messageListener);
        container.addMessageListener(messageListener, new PatternTopic("__keyevent@0__:expired"));
        container.start();
    }

}
