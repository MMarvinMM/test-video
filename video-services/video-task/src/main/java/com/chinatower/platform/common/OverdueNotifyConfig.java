package com.chinatower.platform.common;

import lombok.Data;

import java.util.*;

@Data
public class OverdueNotifyConfig {

    /**
     * 提醒时间间隔配置
     * key: 提醒类型, value: 时间间隔（分钟）
     */
    private static final Map<String, Long> REMINDER_INTERVALS = new LinkedHashMap<>();

    static {
        // 配置提醒间隔（单位：分钟）
        REMINDER_INTERVALS.put("3h", 3 * 60L);      // 3小时
        REMINDER_INTERVALS.put("6h", 6 * 60L);      // 6小时
        REMINDER_INTERVALS.put("12h", 12 * 60L);    // 12小时
        REMINDER_INTERVALS.put("1d", 24 * 60L);     // 1天
        REMINDER_INTERVALS.put("2d", 48 * 60L);     // 2天
    }

    /**
     * 获取所有提醒类型
     */
    public static Set<String> getAllReminderTypes() {
        return REMINDER_INTERVALS.keySet();
    }

    /**
     * 获取提醒间隔（分钟）
     */
    public static Long getReminderInterval(String reminderType) {
        return REMINDER_INTERVALS.get(reminderType);
    }

    public static Map<String, Long> getReminderIntervals() {
        return REMINDER_INTERVALS;
    }

    /**
     * 获取下一个提醒类型
     */
    public static String getNextReminderType(String currentType) {
        List<String> types = new ArrayList<>(REMINDER_INTERVALS.keySet());
        int currentIndex = types.indexOf(currentType);
        return (currentIndex >= 0 && currentIndex < types.size() - 1)
                ? types.get(currentIndex + 1)
                : null;
    }
}
