package com.chinatower.platform.devstatus.item;

import com.chinatower.platform.consumer.item.SensorStatusReport;
import lombok.Data;

import java.util.List;

/**
 * 传感器状态查询返回对象
 * Created by zhujun on 2022/3/31.
 */
@Data
public class SensorStatusRes {
    // 返回编码
    private String returnCode;
    // 返回信息
    private String returnMsg;
    // 错误编码
    private int errorCode;
    // 错误消息
    private int errorMsg;
    // 传感器状态对象
    private List<SensorStatusReport> data;
}
