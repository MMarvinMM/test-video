package com.chinatower.platform.framework.config;

import com.chinatower.platform.project.system.constants.Constants;
import com.chinatower.platform.project.tool.redis.domain.RedisReceiver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.lang.reflect.Method;

@Slf4j
@Configuration
public class RedisSubConfig {

    @Autowired
    private RedisTemplate redisTemplate;


    @Bean
    RedisMessageListenerContainer container (MessageListenerAdapter listenerAdapter){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisTemplate.getConnectionFactory());
        // 可以添加多个 messageListener，配置不同的交换机
        container.addMessageListener(listenerAdapter,new ChannelTopic(Constants.PUB_CHANNEL));
        return  container;
    }

    //消息监听器适配器，绑定消息处理器，利用反射技术调用消息处理器的业务方法
    @Bean
    MessageListenerAdapter listenerAdapter(RedisReceiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }
}
