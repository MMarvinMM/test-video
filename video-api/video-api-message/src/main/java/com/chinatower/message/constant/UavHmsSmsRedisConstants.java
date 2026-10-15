package com.chinatower.message.constant;

/**
 * HMS 飞行告警短信压制相关 Redis Key / Hash 字段常量（video-message 写入，video-task 扫描合并需保持一致）。
 */
public final class UavHmsSmsRedisConstants {

    private UavHmsSmsRedisConstants() {
    }

    /**  Hash 前缀，完整 key = 前缀 + uavCode（deviceCode） */
    public static final String REDIS_KEY_STATE_PREFIX = "uav:hms:sms:state:";

    /** 待合并告警 Hash 前缀，完整 key = 前缀 + uavCode */
    public static final String REDIS_KEY_PENDING_PREFIX = "uav:hms:sms:pending:";

    /** 延迟合并调度 ZSet，member=uavCode，score=截止时间戳（毫秒） */
    public static final String REDIS_KEY_DELAY_ZSET = "uav:hms:sms:delay:zset";

    /** state Hash 中字段：最近一次收到告警的时间（毫秒） */
    public static final String HASH_FIELD_LAST_RECV_AT = "lastRecvAt";

    /**
     * state Hash 中字段：当前 60s 固定窗口起始时间（毫秒）
     * <p>
     * 用于实现“持续告警时每 60s 合并发 1 条”（固定窗口滚动），而不是“距最后一次告警 60s 才发”（滑动窗口）。
     */
    public static final String HASH_FIELD_WINDOW_START_AT = "windowStartAt";

    public static final String REDIS_KEY_UAV_CODE_NAME_PREFIX = "uav:code:name:";

    public static String stateKey(String uavCode) {
        return REDIS_KEY_STATE_PREFIX + uavCode;
    }

    public static String pendingKey(String uavCode) {
        return REDIS_KEY_PENDING_PREFIX + uavCode;
    }

    public static String uavCodeNameKey(String uavCode) {
        return REDIS_KEY_UAV_CODE_NAME_PREFIX + uavCode;
    }
}
