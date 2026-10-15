package com.chinatower.sync.consumer.schedule;

import cn.hutool.core.date.DateUtil;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.sync.consumer.entity.DeviceSyncErrorDto;
import com.chinatower.sync.consumer.item.DeviceSyncConstants;
import com.chinatower.sync.consumer.service.impl.AlgorithmConfigInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 30s定时
 * 取redis保存的channel code信息  更新区域、设备、通道等信息
 *
 * @author yy
 */

@Component
@Slf4j
public class DeviceSyncSchedule {


    @Value("${device.sync.count:500}")
    private int count;

    @Value("${device.sync.errRetry.maxCount:3}")
    private int maxCount;

    private final AlgorithmConfigInfoService algorithmConfigInfoService;

    private final RedisTemplate<String, Object> redisTemplate;

    private final MongoTemplate mongoTemplate;

    public DeviceSyncSchedule(AlgorithmConfigInfoService algorithmConfigInfoService, @Qualifier("primaryMongoTemplate") MongoTemplate mongoTemplate, @Qualifier("redisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.algorithmConfigInfoService = algorithmConfigInfoService;
        this.mongoTemplate = mongoTemplate;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 30s定时
     */
    @Scheduled(fixedRateString = "${device.sync.timing:30000}")
    @Transactional(rollbackFor = Exception.class)
    public void deviceSync5minTask() {
        log.info("设备30秒批量更新定时--开始");
        //上锁
        /*
          redis 锁
         */
        String syncLockKey = "deviceSync5min--Lock";
        if (isLockFromRedis(syncLockKey)) {
            return;
        }
        List<String> list = new ArrayList<>();
        try {
            //redis获取数据
            /*
             *一次定时处理的设备数量
             */
            List<Object> pop = redisTemplate.opsForSet().pop(DeviceSyncConstants.VIDEO_DEVICE_CHANNEL_CODE_LIST_KEY, count);
            if (null != pop && 0 < pop.size()) {
                list = pop.stream().map(String::valueOf).collect(Collectors.toList());
            }
            if (!list.isEmpty()) {
                log.info("本次更新的通道编号:{}", list);
                String[] myList = new String[list.size()];
                String[] array = list.toArray(myList);
                algorithmConfigInfoService.queryYgDeviceAndChannel(array, null);
            }
            //释放锁
            RedisUtil.delete(syncLockKey, RedisDBType.PRIMARY);
            log.info("数据已取出{}条,释放锁 deviceSync5min--Lock",list.size());
        } finally {
            log.info("设备30秒批量更新定时--结束");
        }
    }

    /**
     * 容错机制 未同步成功数据重新请求n次(n采用配置) 如未成功则下一批
     */
    @Scheduled(fixedRateString = "${device.sync.errRetry.timing:30000}")
    @Transactional(rollbackFor = Exception.class)
    public void faultTolerantThird() {
        log.info("设备30秒错误数据重试机制定时--开始");
        //获取锁
        String errRetryLockKey = "deviceErrRetry30s--Lock";
        if (isLockFromRedis(errRetryLockKey)) {
            return;
        }
        String mongoId = null;
        Integer retryCount = 0;
        try {
            //查询 mongo时间最早的一条数据
            Query query = new Query();
            log.info("最大重试次数:{}", maxCount);
            query.addCriteria(Criteria.where("retryCount").lt(maxCount));
            query.with(Sort.by(Sort.Order.asc("date")));
            query.limit(1);
            DeviceSyncErrorDto one = mongoTemplate.findOne(query, DeviceSyncErrorDto.class, DeviceSyncConstants.VIDEO_DEVICE_ERR_LIST);
            log.info("{}",one);
            if (null != one && !StringUtils.isEmpty(one.getId())) {
                mongoId = one.getId();
                retryCount = one.getRetryCount();
                List<String> list = one.getList();
                log.info("本次重试的通道编号:{}", list);
                String[] myList = new String[list.size()];
                String[] array = list.toArray(myList);
                algorithmConfigInfoService.queryYgDeviceAndChannel(array, one);
            } else {
                log.info("当前已无重试数据");
            }
            //释放锁
            RedisUtil.delete(errRetryLockKey, RedisDBType.PRIMARY);
            log.info("设备30秒错误数据重试机制 释放锁 deviceErrRetry30s--Lock");
        } finally {
            log.info("设备30秒错误数据重试机制定时--结束");
        }
    }

    /**
     * 加锁
     */
    private boolean isLockFromRedis(String key) {
        //锁过期时间
        final long expireTimeMin = 3L;
        final String lockValue = "lock";
        //判断是否上锁
        if (!RedisUtil.setIfAbsent(key, lockValue, expireTimeMin, TimeUnit.MINUTES, RedisDBType.PRIMARY)) {
            //如果重复，说明  当前锁被占用，该定时不执行
            log.info("当前有设备批量更新任务 锁:{} 还在执行中 -", key);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 异常 处理
     * 3次同步异常后，进行数据保存并继续执行
     */
    private void deviceSyncErrProcess(List<String> channelCodes) {
        //将错误数据保存到mongo
        DeviceSyncErrorDto deviceSyncErrorDto = new DeviceSyncErrorDto();
        deviceSyncErrorDto.setDate(DateUtil.now());
        deviceSyncErrorDto.setList(channelCodes);
        deviceSyncErrorDto.setRetryCount(0);
        mongoTemplate.insert(deviceSyncErrorDto, DeviceSyncConstants.VIDEO_DEVICE_ERR_LIST);
        log.info("当前{}条数据同步失败,保存到mongo设备同步错误表中,保存的数据为:{}", channelCodes.size(), channelCodes);
    }

    /**
     * 设备同步 重试机制失败 失败次数+1
     * @param id mongo id
     * @param retryCount 本次重试之前的 重试次数
     */
    private void deviceSyncErrUpdateCount(String id, Integer retryCount) {
        if (!StringUtils.isEmpty(id)) {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(id));
            Update update = new Update();
            update.set("retryCount", retryCount + 1);
            mongoTemplate.updateFirst(query, update, DeviceSyncConstants.VIDEO_DEVICE_ERR_LIST);
            log.info("通道重试数据{}失败;失败次数+1,失败数为:{}", id, retryCount + 1);
        }
    }
}
