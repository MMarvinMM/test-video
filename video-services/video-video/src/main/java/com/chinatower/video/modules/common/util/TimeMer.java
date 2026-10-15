package com.chinatower.video.modules.common.util;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.chinatower.video.modules.videorecordplan.dto.TimeBucket;
import com.chinatower.video.modules.videorecordplan.dto.TimeNode;
@Service
public class TimeMer {


    //合并有重叠区别的时间
    public static List<TimeNode> merge(List<TimeBucket> timeBucketList) {
        List<TimeNode> nodeList = new ArrayList<>(2 * timeBucketList.size());
        for (TimeBucket timeBucket : timeBucketList) {
            nodeList.add(new TimeNode(timeBucket.getStart(), 1));
            nodeList.add(new TimeNode(timeBucket.getEnd(), -1));
        }
        nodeList.sort(Comparator.comparing(TimeNode::getTime));
        //设置第一个节点的和
        TimeNode firstNode = nodeList.get(0);
        firstNode.setSum(firstNode.getDirect());
        //计算后续节点的累计和（上一个节点的和与当前节点的方向之和）
        for (int i = 1; i < nodeList.size(); i++) {
            TimeNode preNode = nodeList.get(i - 1);
            TimeNode currentNode = nodeList.get(i);
            currentNode.setSum(preNode.getSum() + currentNode.getDirect());
        }
        //只保留 direct=1 sum=1的开始节点 和 direct=1 sum=0的结束节点
        nodeList.removeIf(n -> !((n.getDirect() == 1 && n.getSum() == 1) || (n.getDirect() == -1 && n.getSum() == 0)));
        return nodeList;
    }

    //将 List<TimeNode> 转换成 List<TimeBucket> 类型
    public static List<TimeBucket> convert(List<TimeNode> nodeList) {
        List<TimeBucket> timeBucketList = new ArrayList<>(nodeList.size() / 2);
        for (int i = 0; i < nodeList.size(); i = i + 2) {
            long start = nodeList.get(i).getTime();
            long end = nodeList.get(i + 1).getTime();
            timeBucketList.add(new TimeBucket(new Date(start), new Date(end)));
        }
        return timeBucketList;
    }


    /**
     * 根据日期区间和星期几返回所有符合条件的日期
     * @param startDateStr 开始日期，格式为"yyyy-MM-dd"
     * @param endDateStr 结束日期，格式为"yyyy-MM-dd"
     * @param dayOfWeek 星期几（1-7，1表示星期一，7表示星期日）
     * @return 符合条件的日期列表，格式为"yyyy-MM-dd"
     */
    public static List<String> findDatesByDayOfWeek(String startDateStr, String endDateStr, int dayOfWeek) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter);

        // 验证星期几输入是否合法
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new IllegalArgumentException("星期几必须在1-7之间，1表示星期一，7表示星期日");
        }

        List<String> resultDates = new ArrayList<>();
        LocalDate currentDate = startDate;

        DayOfWeek targetDay = DayOfWeek.of(dayOfWeek);

        // 调整到第一个匹配的日期
        while (currentDate.getDayOfWeek() != targetDay && !currentDate.isAfter(endDate)) {
            currentDate = currentDate.plusDays(1);
        }

        // 收集所有匹配的日期
        while (!currentDate.isAfter(endDate)) {
            resultDates.add(currentDate.format(formatter));
            // 跳到下周同一天
            currentDate = currentDate.plusWeeks(1);
        }

        return resultDates;
    }


    /**
     * 对日期字符串列表进行去重和排序
     * @param dateStrings 日期字符串列表，格式为"yyyy-MM-dd"
     * @param storageDay 存储天数（可为null）
     * @return 去重并按日期排序的日期字符串列表
     */
    public static List<String> deduplicateAndSortDates(List<String> dateStrings, Integer storageDay) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.now(); // 获取当前日期

        // 使用LinkedHashSet去重并保持插入顺序
        Set<String> uniqueDates = new LinkedHashSet<>(dateStrings);

        // 将去重后的日期字符串转换为LocalDate对象并排序
        List<LocalDate> sortedDates = uniqueDates.stream()
                .map(date -> LocalDate.parse(date, formatter))
                .filter(date -> !date.isAfter(currentDate)) // 过滤未来日期
                .filter(date -> {
                    // 如果指定了存储天数，过滤掉过期的日期
                    if (storageDay != null && storageDay > 0) {
                        // 包含今天，往前推 storageDay-1 天
                        // 例如：storageDay=7，今天=28号，则保留 22,23,24,25,26,27,28
                        LocalDate expireDate = currentDate.minusDays(storageDay - 1);
                        return !date.isBefore(expireDate); // 只保留 >= expireDate 的日期
                    }
                    return true;
                })
                .sorted()
                .collect(Collectors.toList());

        // 将排序后的LocalDate对象转换回字符串
        return sortedDates.stream()
                .map(date -> date.format(formatter))
                .collect(Collectors.toList());
    }


}