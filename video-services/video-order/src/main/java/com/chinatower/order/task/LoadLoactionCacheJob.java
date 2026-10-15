package com.chinatower.order.task;

import com.chinatower.order.service.IWeatherService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName LoadLoactionCacheJob
 * @Deacription 加载区域对照表缓存job
 * @Author chenmf
 * @Date 2022/5/7 10:55
 * @Version 1.0
 **/
@Component
@Slf4j
public class LoadLoactionCacheJob {
    
    @Autowired
    private IWeatherService weatherService;
    
    /**
     * 加载区域对照表缓存
     * @param
     * @return
     */
    @XxlJob("loadLoactionCacheHandler")
    public ReturnT<String> loadLocationRedis(String param) {
        long startTime = System.currentTimeMillis();
        XxlJobLogger.log("loadLoactionCacheHandler开始执行,开始时间为:{}", startTime);
    
        weatherService.loadLocationRedis();
        weatherService.loadAreaRedis("2");
        weatherService.loadAreaRedis("3");
        weatherService.loadAreaRedis("4");
        
        long endTime = System.currentTimeMillis();
        XxlJobLogger.log("loadLoactionCacheHandler执行完成,耗时:{}", endTime - startTime);
        return new ReturnT<>("loadLoactionCacheHandler执行结束");
    }
}
