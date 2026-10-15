package com.chinatower.device.utils;


import com.chinatower.device.dto.TimeBucket;
import com.chinatower.device.dto.TimeNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
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



}