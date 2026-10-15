package com.chinatower.platform.project.system.controller;

import com.chinatower.platform.project.system.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * video-system
 *
 */
@Slf4j
@RestController
public class PubSubTestController {

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/pubMessage")
    public void pubMessage(String message){

        log.info("message:{}", message);
        redisTemplate.convertAndSend(Constants.PUB_CHANNEL, message);
    }
}
