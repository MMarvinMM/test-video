package com.chinatower.event.util.subscenes;

import com.chinatower.event.constants.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 领导批示等无 sub_scenes 列的表：按告警时间范围展开工单月表名，供 EXISTS 过滤。
 */
public final class SubScenesOrderTableHelper {

    /** 仅允许合法月表名，防止 ${} 注入 */
    private static final Pattern SAFE_WARNING_ORDER_TABLE =
            Pattern.compile("^vm_warning_order_t_\\d{6}$");

    private static final String[] PARSE_PATTERNS = {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-M-d HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy-M-d HH:mm",
            "yyyy-MM-dd",
            "yyyy-M-d"
    };

    private SubScenesOrderTableHelper() {
    }

    /**
     * 按告警起止时间列出工单月表（含起止月），表名前缀默认 vm_warning_order_t_。
     */
    public static List<String> listTables(String alarmTimeStart, String alarmTimeEnd) {
        return listTables(alarmTimeStart, alarmTimeEnd, Constants.WARN_ORDER_TEBLE);
    }

    /**
     * @param tablePrefix 分表前缀，如 vm_warning_order_t_
     */
    public static List<String> listTables(String alarmTimeStart, String alarmTimeEnd, String tablePrefix) {
        if (isBlank(alarmTimeStart) || isBlank(alarmTimeEnd) || isBlank(tablePrefix)) {
            return Collections.emptyList();
        }
        Date start = parseFlexible(alarmTimeStart.trim());
        Date end = parseFlexible(alarmTimeEnd.trim());
        if (start == null || end == null) {
            return Collections.emptyList();
        }
        if (start.after(end)) {
            Date tmp = start;
            start = end;
            end = tmp;
        }
        Calendar cursor = Calendar.getInstance();
        cursor.setTime(start);
        cursor.set(Calendar.DAY_OF_MONTH, 1);
        cursor.set(Calendar.HOUR_OF_DAY, 0);
        cursor.set(Calendar.MINUTE, 0);
        cursor.set(Calendar.SECOND, 0);
        cursor.set(Calendar.MILLISECOND, 0);

        Calendar endMonth = Calendar.getInstance();
        endMonth.setTime(end);
        endMonth.set(Calendar.DAY_OF_MONTH, 1);
        endMonth.set(Calendar.HOUR_OF_DAY, 0);
        endMonth.set(Calendar.MINUTE, 0);
        endMonth.set(Calendar.SECOND, 0);
        endMonth.set(Calendar.MILLISECOND, 0);

        SimpleDateFormat yyyyMM = new SimpleDateFormat("yyyyMM");
        List<String> tables = new ArrayList<>();
        // 防止异常大范围：最多 36 个月
        int guard = 0;
        while (!cursor.after(endMonth) && guard++ < 36) {
            String tableName = tablePrefix + yyyyMM.format(cursor.getTime());
            if (SAFE_WARNING_ORDER_TABLE.matcher(tableName).matches()) {
                tables.add(tableName);
            }
            cursor.add(Calendar.MONTH, 1);
        }
        return tables;
    }

    /**
     * 下游任务有值时填充月表列表；无法展开则返回 false（调用方应直接空结果）。
     */
    public static boolean fillWarningOrderTableList(List<String> subSceneIdList,
                                                    String alarmTimeStart,
                                                    String alarmTimeEnd,
                                                    TableListConsumer consumer) {
        if (subSceneIdList == null || subSceneIdList.isEmpty()) {
            return true;
        }
        List<String> tables = listTables(alarmTimeStart, alarmTimeEnd);
        if (tables.isEmpty()) {
            return false;
        }
        consumer.accept(tables);
        return true;
    }

    @FunctionalInterface
    public interface TableListConsumer {
        void accept(List<String> warningOrderTableList);
    }

    private static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    private static Date parseFlexible(String text) {
        for (String pattern : PARSE_PATTERNS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                sdf.setLenient(false);
                return sdf.parse(text);
            } catch (ParseException ignored) {
                // try next
            }
        }
        return null;
    }
}
