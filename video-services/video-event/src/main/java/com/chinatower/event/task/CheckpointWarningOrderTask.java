package com.chinatower.event.task;

import com.chinatower.event.repository.ICheckpointAlarmRepository;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 生产车辆进出记录定时任务
 * 每周一上午9点执行，生成演示数据
 * 使用Redis分布式锁确保多实例部署时只有一个实例执行
 */
@Slf4j
@Component
public class CheckpointWarningOrderTask {

    @Resource
    private ICheckpointAlarmRepository checkpointAlarmRepository;

    private static final String LOCK_KEY = "checkpoint:warning:order:task:lock";
    private static final String LOCK_VALUE = "1";
    private static final long LOCK_TIMEOUT = 3;

    @Scheduled(cron = "0 0 9 ? * MON")
    public void generateCheckpointWarningOrder() {
        boolean locked = false;
        try {
            locked = RedisUtil.setIfAbsent(LOCK_KEY, LOCK_VALUE, LOCK_TIMEOUT, TimeUnit.MINUTES, RedisDBType.PRIMARY);
        } catch (Exception e) {
            log.error("Redis获取锁异常，跳过本次执行: {}", e.getMessage());
            return;
        }
        
        if (!locked) {
            log.info("未能获取锁，其他实例正在执行定时任务");
            return;
        }
        
        log.info("========== 生产车辆进出记录定时任务开始执行 ==========");
        try {
            String result = checkpointAlarmRepository.batchAddWarningOrder(null);
            log.info("批量添加结果: {}", result);
            log.info("========== 生产车辆进出记录定时任务执行成功 ==========");
        } catch (Exception e) {
            log.error("生产车辆进出记录定时任务执行失败", e);
        } finally {
            try {
                RedisUtil.delete(LOCK_KEY, RedisDBType.PRIMARY);
            } catch (Exception e) {
                log.warn("Redis释放锁异常: {}", e.getMessage());
            }
        }
    }
}
