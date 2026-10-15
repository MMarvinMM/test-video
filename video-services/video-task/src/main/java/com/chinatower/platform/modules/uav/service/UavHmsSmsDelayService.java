package com.chinatower.platform.modules.uav.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.IAirPlanService;
import com.chinatower.message.constant.UavHmsSmsRedisConstants;
import com.chinatower.platform.entity.request.CommonSmsRequest;
import com.chinatower.platform.fegin.client.MsgFeignClient;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * HMS 飞行告警短信压制：到期合并发送（由 XXL-JOB 触发）
 */
@Service
@Slf4j
public class UavHmsSmsDelayService {

    private static final long WINDOW_MS = 60000L;

    @Resource
    private MsgFeignClient msgFeignClient;

    @Resource
    private IAirPlanService airPlanService;

    @Value("${sms.uavHms.templateCode:smsvideo20230824584}")
    private String templateCode;

    public void scanAndHandleDue() {
        long now = System.currentTimeMillis();
        Set<ZSetOperations.TypedTuple<String>> due = RedisUtil.zRangeByScoreWithScores(UavHmsSmsRedisConstants.REDIS_KEY_DELAY_ZSET, 0D, Double.valueOf(now), RedisDBType.PRIMARY);
        if (due == null || due.isEmpty()) {
            log.info("HMS短信压制-扫描到期ZSET为空 zsetKey={}, now={}", UavHmsSmsRedisConstants.REDIS_KEY_DELAY_ZSET, now);
            return;
        }
        log.info("HMS短信压制-扫描到期ZSET size={}, zsetKey={}, now={}", due.size(), UavHmsSmsRedisConstants.REDIS_KEY_DELAY_ZSET, now);
        for (ZSetOperations.TypedTuple<String> tuple : due) {
            CompletableFuture.runAsync(() -> {
                String uavCode = tuple.getValue();
                // 先移除避免重复处理
                Long removed = RedisUtil.zRemove(UavHmsSmsRedisConstants.REDIS_KEY_DELAY_ZSET, RedisDBType.PRIMARY, uavCode);
                if (removed == null || removed <= 0) {
                    log.info("HMS短信压制-移除ZSET失败(可能已被其他实例处理) uavCode={}, removed={}", uavCode, removed);
                } else {
                    log.info("HMS短信压制-开始处理到期项 uavCode={}, score={}", uavCode, tuple.getScore());
                    handleOne(uavCode, now);
                }
            });
        }

    }

    private void handleOne(String uavCode, long now) {
        // 用于保存最近一次收到告警的时间key
        String stateKey = UavHmsSmsRedisConstants.stateKey(uavCode);
        // 用于保存待合并告警的key，发送短信使用
        String pendingKey = UavHmsSmsRedisConstants.pendingKey(uavCode);

        Long lastRecvAt = parseLong(RedisUtil.hGet(stateKey, UavHmsSmsRedisConstants.HASH_FIELD_LAST_RECV_AT, RedisDBType.PRIMARY));
        Long windowStartAt = parseLong(RedisUtil.hGet(stateKey, UavHmsSmsRedisConstants.HASH_FIELD_WINDOW_START_AT, RedisDBType.PRIMARY));
        if (lastRecvAt == null || windowStartAt == null) {
            log.info("HMS短信压制-状态缺失(跳过) uavCode={}, stateKey={}, lastRecvAt={}, windowStartAt={}", uavCode, stateKey, lastRecvAt, windowStartAt);
            return;
        }

        List<String> userPhones = queryPhones(uavCode);
        if (CollUtil.isEmpty(userPhones)) {
            log.info("HMS短信压制-到期未找到安全员手机号 uavCode={}", uavCode);
            return;
        }
        log.info("HMS短信压制-查询安全员手机号成功 uavCode={}, userPhones={}", uavCode, userPhones);
        String uavName = getUavNameByUavCodeWithCache(uavCode);
        if (StrUtil.isBlank(uavName)) {
            log.info("HMS-未找到对应的无人机 uavCode={}", uavCode);
            return;
        }
        log.info("HMS短信压制-uavName解析 uavCode={}, uavName={}", uavCode, uavName);
        long windowDeadline = windowStartAt + WINDOW_MS;
        // 未到期：重新塞回 zset（固定窗口截止时间）
        if (now < windowDeadline) {
            RedisUtil.zAdd(UavHmsSmsRedisConstants.REDIS_KEY_DELAY_ZSET, uavCode, Double.valueOf(windowDeadline), RedisDBType.PRIMARY);
            log.info("HMS短信压制-未到期回填ZSET(固定窗口) uavCode={}, windowStartAt={}, now={}, windowDeadline={}", uavCode, windowStartAt, now, windowDeadline);
            return;
        }
        Map<Object, Object> code2Type = RedisUtil.hGetAll(pendingKey, RedisDBType.PRIMARY);
        if (code2Type == null || code2Type.isEmpty()) {
            log.info("HMS短信压制-pending为空(清理并跳过) uavCode={}, pendingKey={}", uavCode, pendingKey);
            // 固定窗口到期但无 pending：若已静默 >=60s 则结束本轮，否则开启下一窗口
            rollOrCleanupAfterWindow(uavCode, now, lastRecvAt, windowStartAt, stateKey, pendingKey);
            return;
        }
        log.info("HMS短信压制-读取pending成功 uavCode={}, pendingSize={}, pendingKey={}", uavCode, code2Type.size(), pendingKey);

        // code 去重：hash 已保证；type 二次去重（按 hmsLevel 倒序优先）
        List<TypeWithLevel> typed = code2Type.values().stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(this::parseTypeWithLevel)
                .filter(t -> StringUtils.isNotEmpty(t.type))
                .sorted(Comparator.comparingInt((TypeWithLevel t) -> t.level).reversed())
                .collect(Collectors.toList());

        LinkedHashSet<String> typeSet = new LinkedHashSet<>();
        for (TypeWithLevel t : typed) {
            typeSet.add(t.type);
        }
        List<String> types = new ArrayList<>(typeSet);
        if (types.isEmpty()) {
            log.info("HMS短信压制-类型去重后为空(清理并跳过) uavCode={}, pendingKey={}", uavCode, pendingKey);
            rollOrCleanupAfterWindow(uavCode, now, lastRecvAt, windowStartAt, stateKey, pendingKey);
            return;
        }
        log.info("HMS短信压制-到期合并类型 uavCode={}, typeCount={}, types={}", uavCode, types.size(), types);

        List<CommonSmsRequest> smsRecords = new ArrayList<>();
        userPhones.forEach(phone -> {
            CommonSmsRequest req = buildSmsRequest(phone, now, uavCode, uavName, types);
            smsRecords.add(req);
        });
        try {
            msgFeignClient.batchSendMessage(smsRecords);
            log.info("HMS短信压制-到期合并短信已发送 uavCode={}, userPhones={}", uavCode, userPhones);
        } catch (Exception e) {
            log.error("HMS短信压制-到期合并短信发送失败 uavCode={}, userPhones={}", uavCode, userPhones, e);
            // 发送失败不清理，避免丢告警（等待下次扫描重试）
            RedisUtil.zAdd(UavHmsSmsRedisConstants.REDIS_KEY_DELAY_ZSET, uavCode, Double.valueOf(now + 5000L), RedisDBType.PRIMARY);
            return;
        }
        // 发送成功：清空 pending，若告警仍持续则进入下一窗口；否则清理 state 结束
        rollOrCleanupAfterWindow(uavCode, now, lastRecvAt, windowStartAt, stateKey, pendingKey);
    }

    private void rollOrCleanupAfterWindow(String uavCode, long now, long lastRecvAt, long windowStartAt, String stateKey, String pendingKey) {
        // 清空本窗口 pending
        RedisUtil.delete(pendingKey, RedisDBType.PRIMARY);

        boolean stillAlarming = (now - lastRecvAt) < WINDOW_MS;
        if (!stillAlarming) {
            RedisUtil.delete(stateKey, RedisDBType.PRIMARY);
            log.info("HMS短信压制-窗口结束且已静默，清理state并结束 uavCode={}, stateKey={}", uavCode, stateKey);
            return;
        }

        // 继续下一窗口：窗口起点按上一个窗口的截止时间推进，保证“持续告警每分钟一条”
        long nextWindowStartAt = windowStartAt + WINDOW_MS;
        RedisUtil.hset(stateKey, UavHmsSmsRedisConstants.HASH_FIELD_WINDOW_START_AT, String.valueOf(nextWindowStartAt), RedisDBType.PRIMARY);
        RedisUtil.expire(stateKey, 3, TimeUnit.MINUTES, RedisDBType.PRIMARY);

        long nextDeadline = nextWindowStartAt + WINDOW_MS;
        RedisUtil.zAdd(UavHmsSmsRedisConstants.REDIS_KEY_DELAY_ZSET, uavCode, Double.valueOf(nextDeadline), RedisDBType.PRIMARY);
        log.info("HMS短信压制-持续告警进入下一窗口 uavCode={}, nextWindowStartAt={}, nextDeadline={}", uavCode, nextWindowStartAt, nextDeadline);
    }

    private TypeWithLevel parseTypeWithLevel(String v) {
        // 兼容历史数据：value 可能是 "alarmType" 或 "level|alarmType"
        if (StringUtils.isEmpty(v)) {
            return new TypeWithLevel(0, "");
        }
        int idx = v.indexOf('|');
        if (idx <= 0) {
            return new TypeWithLevel(0, v.trim());
        }
        String levelStr = v.substring(0, idx).trim();
        String type = v.substring(idx + 1).trim();
        int level = 0;
        try {
            level = Integer.parseInt(levelStr);
        } catch (Exception ignore) {
            level = 0;
        }
        return new TypeWithLevel(level, type);
    }

    private static final class TypeWithLevel {
        private final int level;
        private final String type;

        private TypeWithLevel(int level, String type) {
            this.level = level;
            this.type = type;
        }
    }

    private String getUavNameByUavCodeWithCache(String uavCode) {
        String uavCodeNameKey = UavHmsSmsRedisConstants.uavCodeNameKey(uavCode);
        String uavName = RedisUtil.get(uavCodeNameKey, RedisDBType.PRIMARY);
        if (StringUtils.isNotEmpty(uavName)) {
            return uavName;
        }
        Result<String> result = airPlanService.queryUavNameByUavCode(uavCode);
        if (result == null || result.getData() == null) {
            return null;
        }
        uavName = result.getData();
        if (StringUtils.isNotEmpty(uavName)) {
            RedisUtil.set(uavCode, uavName, RedisDBType.PRIMARY);
            RedisUtil.expire(uavCodeNameKey, 60, TimeUnit.MINUTES, RedisDBType.PRIMARY);
        }
        return uavName;
    }

    private CommonSmsRequest buildSmsRequest(String phone, long now, String uavCode, String uavName, List<String> types) {
        // 【铁塔视联】${dateTime}，${uavName}|${uavCode}无人机发生${riskInfo}，请及时登录系统飞行控制模块查看并处理！
        Map<String, Object> map = new HashMap<>();
        map.put("dateTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date(now)));
        map.put("uavName", uavName);
        map.put("uavCode", uavCode);
        map.put("riskInfo", (types.size() > 2 ? String.join("、", types.subList(0, 2)) : String.join("、", types)) + (types.size() > 2 ? "等" : ""));
        CommonSmsRequest req = new CommonSmsRequest();
        req.setMsgType("2"); // 短信
        req.setPhone(phone);
        req.setRequestMap(JSON.toJSONString(map));
        req.setTemplateCode(templateCode);
        return req;
    }

    private List<String> queryPhones(String uavCode) {
        try {
            com.chinatower.common.entity.Result<List<String>> res = airPlanService.querySafetyOfficerPhonesByFlyingUav(uavCode);
            if (res == null || res.getData() == null) {
                return Collections.emptyList();
            }
            return res.getData();
        } catch (Exception e) {
            log.error("HMS短信压制-查询安全员手机号失败 uavCode={}", uavCode, e);
            return Collections.emptyList();
        }
    }

    private Long parseLong(Object v) {
        if (v == null) {
            return null;
        }
        try {
            return Long.parseLong(String.valueOf(v));
        } catch (Exception ignore) {
            return null;
        }
    }
}

