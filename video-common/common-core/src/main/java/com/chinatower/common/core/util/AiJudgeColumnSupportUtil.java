package com.chinatower.common.core.util;

import org.springframework.util.StringUtils;

/**
 * 告警分级字段兼容：按配置生效月份判断月表是否包含 AI 研判列。
 */
public final class AiJudgeColumnSupportUtil {

    /** 未配置时默认不启用，避免误查不存在的列 */
    private static String columnSinceMonth = "202608";

    private AiJudgeColumnSupportUtil() {
    }

    /**
     * 由 Spring 配置注入生效月份（YYYYMM）
     */
    public static void setColumnSinceMonth(String month) {
        if (StringUtils.hasText(month)) {
            columnSinceMonth = month.trim();
        }
    }

    /**
     * 月表是否支持 AI 研判独立字段（供 MyBatis OGNL / 业务代码调用）
     *
     * @param tableName 如 vm_warning_order_t_202608
     */
    public static boolean isSupported(String tableName) {
        return isMonthSupported(extractMonthFromTableName(tableName));
    }

    /**
     * 按告警工单号判断（工单号前 6 位为 YYYYMM）
     */
    public static boolean isSupportedByOrderId(String orderId) {
        if (!StringUtils.hasText(orderId) || orderId.length() < 6) {
            return false;
        }
        return isMonthSupported(orderId.substring(0, 6));
    }

    private static boolean isMonthSupported(String month) {
        if (!StringUtils.hasText(month) || month.length() != 6) {
            return false;
        }
        return month.compareTo(columnSinceMonth) >= 0;
    }

    private static String extractMonthFromTableName(String tableName) {
        if (!StringUtils.hasText(tableName)) {
            return null;
        }
        int idx = tableName.lastIndexOf('_');
        if (idx < 0 || idx + 1 >= tableName.length()) {
            return null;
        }
        String suffix = tableName.substring(idx + 1);
        return suffix.length() == 6 && suffix.chars().allMatch(Character::isDigit) ? suffix : null;
    }
}
