package com.chinatower.bdm.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.SecureRandom;
import java.util.List;

public class NonceUtil {

    private NonceUtil(){

    }
    private static final Logger logger = LoggerFactory.getLogger(NonceUtil.class);





    /**
     * 获取时间戳(秒)
     */
    public static String getTimestamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    /**
     * 取出一个指定长度大小的随机正整数.
     *
     * @param length int 设定所取出随机数的长度。length小于11
     * @return int 返回生成的随机数。
     */
    private static int buildRandom(int length) {
        int num = 1;
        double random = new SecureRandom().nextDouble();
        if (random < 0.1) {
            random = random + 0.1;
        }
        for (int i = 0; i < length; i++) {
            num = num * 10;
        }
        return (int) (random * num);
    }

    /**
     * 获取当前时间 yyyyMMddHHmmss
     */
    private static String getCurrTime() {
        Date now = new Date();
        SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return outFormat.format(now);
    }

    /**
     * 生成随机字符串
     */
    public static String getNonceStr() {
        String currTime = getCurrTime();
        //String strTime = currTime.substring(8, currTime.length());
        String strRandom = buildRandom(4) + "";
        return currTime + strRandom;
    }

    /**
     * 获取当前时间
     */
    public static String getCurDateTime() {
        Date date = new Date();
        String strDateFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        return sdf.format(date);
    }

    // 判断时间段是否存在重叠
    public static boolean checkTimeRangesOverlap(String input) {
        String[] ranges = input.split(",");
        List<int[]> timeRanges = new ArrayList<>();

        for (String range : ranges) {
            String[] parts = range.split("~");
            String[] startParts = parts[0].split(":");
            String[] endParts = parts[1].split(":");

            int startHour = Integer.parseInt(startParts[0]);
            int startMinute = Integer.parseInt(startParts[1]);
            int endHour = Integer.parseInt(endParts[0]);
            int endMinute = Integer.parseInt(endParts[1]);

            timeRanges.add(new int[]{startHour, startMinute, endHour, endMinute});
        }

        boolean hasOverlap = false;
        for (int i = 0; i < timeRanges.size(); i++) {
            for (int j = i + 1; j < timeRanges.size(); j++) {
                if (overlaps(timeRanges.get(i), timeRanges.get(j))) {
                    hasOverlap = true;
                    break;
                }
            }
            if (hasOverlap) {
                break;
            }
        }

        return hasOverlap;
    }

    private static boolean overlaps(int[] range1, int[] range2) {
        return (range1[0] < range2[2] || (range1[0] == range2[2] && range1[1] <= range2[3])) &&
                (range1[2] > range2[0] || (range1[2] == range2[0] && range1[3] >= range2[1]));
    }


    public static boolean areAllValidTimeRanges(String input) {
        String[] ranges = input.split(",");
        for (String range : ranges) {
            if (!isValidTimeRange(range)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidTimeRange(String range) {
        String[] parts = range.split("~");
        String[] startParts = parts[0].split(":");
        String[] endParts = parts[1].split(":");

        int startHour = Integer.parseInt(startParts[0]);
        int startMinute = Integer.parseInt(startParts[1]);
        int endHour = Integer.parseInt(endParts[0]);
        int endMinute = Integer.parseInt(endParts[1]);

        // 处理跨越日期的情况
        if (endHour < startHour || (endHour == startHour && endMinute <= startMinute)) {
            return false;
        }
        return true;
    }


}
