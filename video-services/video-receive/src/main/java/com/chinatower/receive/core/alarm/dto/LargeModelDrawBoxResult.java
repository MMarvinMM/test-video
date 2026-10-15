package com.chinatower.receive.core.alarm.dto;

import com.chinatower.common.core.dto.BoxInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 大模型告警画框合并结果
 */
@Getter
@AllArgsConstructor
public class LargeModelDrawBoxResult {

    /**
     * 最终参与绘制的框列表
     */
    private final List<BoxInfo> boxList;

    /**
     * 是否应产生告警（上传图片并发送 Kafka）
     */
    private final boolean shouldAlarm;

    /**
     * 跳过告警原因（shouldAlarm=false 时有值）
     */
    private final String skipReason;
}
