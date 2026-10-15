package com.chinatower.message.hms.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.constant.Constants;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.message.constant.UavHmsSmsRedisConstants;
import com.chinatower.message.station.entity.UavEventHms;
import com.chinatower.device.api.IAirPlanService;
import com.chinatower.platform.entity.request.CommonSmsRequest;
import com.chinatower.platform.fegin.client.MsgFeignClient;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * HMS 飞行阶段短信压制：最小连续告警间隔 60s，每 60s 最多 1 条短信
 * <p>
 * 触发规则：
 * - 首次/冷却后：立即发送 1 条
 * - 窗口内：不立即发送；距离上次告警 60s 到期时合并发送 1 条（code 去重 -> type 去重）
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UavHmsSmsSuppressService {

    private static final long WINDOW_MS = 60000L;

    private final IAirPlanService airPlanService;
    private final MsgFeignClient msgFeignClient;
    private final UavHmsAlarmTypeResolver alarmTypeResolver;

    @Value("${sms.uavHms.templateCode:smsvideo20230824584}")
    private String templateCode;

    /**
     * Kafka 收到告警时调用
     */
    public void onAlarm(UavEventHms event, long eventTimestampMs) {
        if (event == null || StringUtils.isEmpty(event.getDeviceCode())) {
            log.info("HMS短信-收到告警为空或缺少deviceCode，忽略 event={}", event == null ? null : JSON.toJSONString(event));
            return;
        }
        // 仅飞行阶段：在天上=1
        if (!Constants.ONE.equals(event.getInTheSky())) {
            log.info("HMS短信-非飞行阶段忽略 uavCode={}, inTheSky={}", event.getDeviceCode(), event.getInTheSky());
            return;
        }

        String uavCode = event.getDeviceCode();
        List<String> userPhones = queryPhones(uavCode);
        if (CollectionUtil.isEmpty(userPhones)) {
            log.info("HMS短信-未找到安全员手机号，uavCode={}", uavCode);
            return;
        }
        String uavName = getUavNameByUavCodeWithCache(uavCode);
        if(StrUtil.isBlank(uavName)){
            log.info("HMS-未找到对应的无人机 uavCode={}",uavCode);
            return;
        }
        long now = eventTimestampMs > 0 ? eventTimestampMs : System.currentTimeMillis();
        // 用于保存最近一次收到告警的时间key
        String stateKey = UavHmsSmsRedisConstants.stateKey(uavCode);
        // 用于保存待合并告警的key，发送短信使用
        String pendingKey = UavHmsSmsRedisConstants.pendingKey(uavCode);

        Long lastRecvAt = readLongHash(stateKey, UavHmsSmsRedisConstants.HASH_FIELD_LAST_RECV_AT);
        Long windowStartAt = readLongHash(stateKey, UavHmsSmsRedisConstants.HASH_FIELD_WINDOW_START_AT);
        // 冷却判断：无历史，或距上次告警已超过 60s（中间静默满 1 分钟）→ 本条视为「首次/冷却后」，可立即发短信并开启新窗口
        boolean firstOrCooledDown = lastRecvAt == null || (now - lastRecvAt) >= WINDOW_MS;

        String hmsCode = StrUtil.trim(event.getHmsCode());
        String alarmType = StrUtil.trim(alarmTypeResolver.resolveType(event.getHmsCode()));
        if (StrUtil.isBlank(alarmType)) {
            log.info("HMS-告警类型为空，uavCode={}, hmsCode={}", uavCode, hmsCode);
            return;
        }

        int hmsLevel = parseHmsLevel(event.getHmsLevel());
        log.info("HMS短信-收到告警 uavCode={}, hmsCode={}, alarmType={}, lastRecvAt={}, windowStartAt={}, now={}, firstOrCooledDown={}",
                uavCode, hmsCode, alarmType, lastRecvAt, windowStartAt, now, firstOrCooledDown);

        // value 按 "hmsLevel|alarmType" 保存，合并发送侧会按 level 倒序排序后再做类型去重
        RedisUtil.hset(pendingKey, hmsCode, hmsLevel + "|" + alarmType, RedisDBType.PRIMARY);
        RedisUtil.expire(pendingKey, 3, TimeUnit.MINUTES, RedisDBType.PRIMARY);

        // 维护 lastRecvAt（每次来告警都刷新为 now）
        RedisUtil.hset(stateKey, UavHmsSmsRedisConstants.HASH_FIELD_LAST_RECV_AT, String.valueOf(now), RedisDBType.PRIMARY);
        RedisUtil.expire(stateKey, 3, TimeUnit.MINUTES, RedisDBType.PRIMARY);

        // 固定窗口：只在首次/冷却后设置 windowStartAt；窗口内不滚动 deadline
        if (firstOrCooledDown || windowStartAt == null) {
            windowStartAt = now;
            RedisUtil.hset(stateKey, UavHmsSmsRedisConstants.HASH_FIELD_WINDOW_START_AT, String.valueOf(windowStartAt), RedisDBType.PRIMARY);
            RedisUtil.expire(stateKey, 3, TimeUnit.MINUTES, RedisDBType.PRIMARY);
        }
        long deadline = windowStartAt + WINDOW_MS;
        // 延迟合并：score=窗口截止时间（固定窗口）
        RedisUtil.zAdd(UavHmsSmsRedisConstants.REDIS_KEY_DELAY_ZSET, uavCode, Double.valueOf(deadline), RedisDBType.PRIMARY);
        log.info("HMS短信-写入压制状态(固定窗口) uavCode={}, stateKey={}, pendingKey={}, windowStartAt={}, deadline={}",
                uavCode, stateKey, pendingKey, windowStartAt, deadline);

        if (firstOrCooledDown) {
            boolean sent = sendImmediateSms(uavCode, uavName, event.getHmsCode(), userPhones, now);
            if (sent) {
                // 首次/冷却后已立即发送：避免本次告警进入后续 60s 合并短信，再次下发
                cleanUp(pendingKey, hmsCode, uavCode);
            }
        }
    }

    private static void cleanUp(String pendingKey, String hmsCode, String uavCode) {
        try {
            RedisUtil.hDelete(pendingKey, RedisDBType.PRIMARY, hmsCode);
            log.info("HMS短信-首次/冷却后已发送，清理pending本次告警 uavCode={}, hmsCode={}, pendingKey={}", uavCode, hmsCode, pendingKey);
        } catch (Exception e) {
            log.error("HMS短信-首次/冷却后已发送，清理pending失败 uavCode={}, hmsCode={}, pendingKey={}", uavCode, hmsCode, pendingKey, e);
        }
    }

    private int parseHmsLevel(String hmsLevel) {
        if (StrUtil.isBlank(hmsLevel)) {
            return 0;
        }
        try {
            return Integer.parseInt(hmsLevel.trim());
        } catch (Exception ignore) {
            return 0;
        }
    }


    private boolean sendImmediateSms(String uavCode, String uavName, String hmsCode, List<String> userPhones, Long now) {
        String alarmType = StrUtil.trim(alarmTypeResolver.resolveType(hmsCode));
        if (StrUtil.isBlank(alarmType)) {
            log.info("HMS短信-立即发送告警类型为空，跳过 uavCode={}, hmsCode={}", uavCode, StrUtil.trim(hmsCode));
            return false;
        }
        log.info("HMS短信-立即发送准备 uavCode={}, uavName={}, alarmType={}, phoneCount={}", uavCode, uavName, alarmType, userPhones.size());
        List<CommonSmsRequest> smsRecords = new ArrayList<>();
        userPhones.forEach(phone -> {
            CommonSmsRequest req = buildSmsRequest(phone, uavCode, uavName, alarmType, now);
            smsRecords.add(req);
        });
        try {
            msgFeignClient.batchSendMessage(smsRecords);
            log.info("HMS短信-立即发送完成 uavCode={}, userPhones={}", uavCode, userPhones);
            return true;
        } catch (Exception e) {
            log.error("HMS短信-立即发送失败 uavCode={}, userPhones={}", uavCode, userPhones, e);
            return false;
        }
    }

    private String getUavNameByUavCodeWithCache(String uavCode) {
        String uavCodeNameKey = UavHmsSmsRedisConstants.uavCodeNameKey(uavCode);
        String uavName = RedisUtil.get(uavCodeNameKey, RedisDBType.PRIMARY);
        if (StringUtils.isNotEmpty(uavName)) {
            log.info("HMS短信-uavName缓存命中 uavCode={}, uavName={}", uavCode, uavName);
            return uavName;
        }
        log.info("HMS短信-uavName缓存未命中，开始查询 uavCode={}", uavCode);
        Result<String> result = airPlanService.queryUavNameByUavCode(uavCode);
        if (result == null || result.getData() == null) {
            log.info("HMS短信-uavName查询结果为空 uavCode={}, result={}", uavCode, result == null ? null : JSON.toJSONString(result));
            return null;
        }
        uavName = result.getData();
        if (StringUtils.isNotEmpty(uavName)) {
            RedisUtil.set(uavCode, uavName, RedisDBType.PRIMARY);
            RedisUtil.expire(uavCodeNameKey, 60, TimeUnit.MINUTES, RedisDBType.PRIMARY);
            log.info("HMS短信-uavName写入缓存 uavCode={}, key={}, expireMinutes={}, uavName={}", uavCode, uavCodeNameKey, 60, uavName);
        }
        return uavName;
    }

    private CommonSmsRequest buildSmsRequest(String phone, String uavCode, String uavName, String alarmType, Long now) {
        // 【铁塔视联】${dateTime}，${uavName}|${uavCode}无人机发生${riskInfo}，请及时登录系统飞行控制模块查看并处理！
        Map<String, Object> map = new HashMap<>();
        map.put("dateTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date(now)));
        map.put("uavName", uavName);
        map.put("uavCode", uavCode);
        map.put("riskInfo", alarmType);

        CommonSmsRequest req = new CommonSmsRequest();
        req.setMsgType("2"); // 短信
        req.setPhone(phone);
        req.setRequestMap(JSON.toJSONString(map));
        req.setTemplateCode(templateCode);
        return req;
    }


    private Long readLongHash(String key, String field) {
        Object val = RedisUtil.hGet(key, field, RedisDBType.PRIMARY);
        if (val == null) {
            return null;
        }
        try {
            return Long.parseLong(String.valueOf(val));
        } catch (Exception ignore) {
            return null;
        }
    }

    private List<String> queryPhones(String uavCode) {
        try {
            com.chinatower.common.entity.Result<List<String>> res = airPlanService.querySafetyOfficerPhonesByFlyingUav(uavCode);
            if (res == null || res.getData() == null) {
                log.info("HMS短信-查询安全员手机号结果为空 uavCode={}, res={}", uavCode, res == null ? null : JSON.toJSONString(res));
                return Collections.emptyList();
            }
            log.info("HMS短信-查询安全员手机号成功 uavCode={}, phoneCount={}", uavCode, res.getData().size());
            return res.getData();
        } catch (Exception e) {
            log.error("HMS短信-查询安全员手机号失败 uavCode={}", uavCode, e);
            return Collections.emptyList();
        }
    }

}

