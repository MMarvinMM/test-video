package com.chinatower.receive.util;

import com.chinatower.event.util.DateUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: yuexuebin
 * @Date: 2021/10/29 09:54
 * @Description:
 */
@Component
public class IPKUtils {

    private static String YEAR_MONTH_ORDER_KEY = "yearMonthOrder";

    private static String ORDER_KEY = "order";

    private static String POINT_KEY = "point";

    private static String PROCESS_KEY = "process";

    private static String FILE_KEY = "file";

    private static final String SEP = "-";

    private static final String YYYYMMDD_FORMAT = "yyyyMMdd";


    @Autowired
    private static RedisUtil redisUtil;

    /**
     * 功能描述: 生成32位工单流程告警id 规则： yyyyMMdd+customerId+递增id 202110121059785
     *
     * @param: [customerId]
     * @return: java.lang.String
     * @auther: yuexuebin
     * @date: 2021/10/29 9:55
     */
    public static String getFlowOrderId(String customerId, String yearMonth) {
        StringBuffer sbf = new StringBuffer();
        try {
            int hincr = Double.valueOf(RedisUtil.hIncrBy(YEAR_MONTH_ORDER_KEY, PROCESS_KEY, 1, RedisDBType.PRIMARY)).intValue();
            RedisUtil.expire(YEAR_MONTH_ORDER_KEY, 30 * 24 * 60 * 60, TimeUnit.SECONDS, RedisDBType.PRIMARY);
            int length = 24 - yearMonth.length() - customerId.length();
            String id = String.format("%0" + length + "d", hincr);
            sbf.append(yearMonth).append(customerId).append(id);
            return sbf.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return sbf.append(yearMonth).append(UUID.randomUUID().toString()).toString();
        }
    }

    /**
     * 生成框图唯一标识
     * @param customerId
     * @author tianhao
     * @return
     */
    public static String getAlarmPictureId(String customerId) {
        StringBuffer sbf = new StringBuffer();
        String yearMonth = DateUtils.date2Str(new Date(), YYYYMMDD_FORMAT);
        try {
            int hincr = Double.valueOf(RedisUtil.hIncrBy(YEAR_MONTH_ORDER_KEY, POINT_KEY, 1, RedisDBType.PRIMARY)).intValue();
            RedisUtil.expire(YEAR_MONTH_ORDER_KEY, 30 * 24 * 60 * 60, TimeUnit.SECONDS, RedisDBType.PRIMARY);
            int length = 32 - yearMonth.length() - customerId.length();
            String id = String.format("%0" + length + "d", hincr);
            sbf.append(yearMonth).append(customerId).append(id);
            return sbf.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return sbf.append(yearMonth).append(UUID.randomUUID().toString()).toString();
        }
    }

    /**
     * 功能描述: 生成32位工单流程告警id 规则： yyyyMMdd+customerId+递增id 202110121059785
     *
     * @param: [customerId]
     * @return: java.lang.String
     * @auther: yuexuebin
     * @date: 2021/10/29 9:55
     */
    public static String getOrderFileId(String customerId, String yearMonth) {
        StringBuffer sbf = new StringBuffer();
        try {
            int hincr = Double.valueOf(RedisUtil.hIncrBy(YEAR_MONTH_ORDER_KEY, FILE_KEY, 1, RedisDBType.PRIMARY)).intValue();
            RedisUtil.expire(YEAR_MONTH_ORDER_KEY, 30 * 24 * 60 * 60, TimeUnit.SECONDS, RedisDBType.PRIMARY);
            int length = 24 - yearMonth.length() - customerId.length();
            String id = String.format("%0" + length + "d", hincr);
            sbf.append(yearMonth).append(customerId).append(id);
            return sbf.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return sbf.append(yearMonth).append(UUID.randomUUID().toString()).toString();
        }
    }

    /**
     * 功能描述: 生成32位工单告警id 规则： yyyyMMdd+customerId+递增id 202110121059785
     *
     * @param: [customerId]
     * @return: java.lang.String
     * @auther: luo.zhenghong
     * @date: 2022/8/4 9:55
     */
    public static String getWarnOrderId(String customerId, String date) {
        StringBuffer sbf = new StringBuffer();
        String yearMonth = DateUtils.date2Str(DateUtils.string2Date(date), YYYYMMDD_FORMAT);
        try {
            int hincr = Double.valueOf(RedisUtil.hIncrBy(YEAR_MONTH_ORDER_KEY, ORDER_KEY, 1, RedisDBType.PRIMARY)).intValue();
            RedisUtil.expire(YEAR_MONTH_ORDER_KEY, 6 * 30 * 24 * 60 * 60, TimeUnit.SECONDS, RedisDBType.PRIMARY);
            int length = 24 - yearMonth.length() - customerId.length();
            String id = String.format("%0" + length + "d", hincr);
            sbf.append(yearMonth).append(customerId).append(id);
            return sbf.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return sbf.append(yearMonth).append(UUID.randomUUID().toString()).toString();
        }
    }
}
