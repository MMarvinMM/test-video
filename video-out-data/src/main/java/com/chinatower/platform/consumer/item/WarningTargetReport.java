package com.chinatower.platform.consumer.item;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

/**
 * 传感器状态数据对象
 * Created by zhujun on 2022/3/31.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WarningTargetReport extends BaseMongoDTO {
    /**
     * [
     *   {
     *     "alarmType": 2121103,
     *     "alarmTime": "2023-05-27 01:17:15",
     *     "alarmBody": "电子围栏低速",
     *     "alarmCode": "faad558792914bb5aadc9029d65a04a8b902936cb1e34b199be05aecce2586a6",
     *     "taskId": "16851152173976idzr",
     *     "importanceLevel": 1,
     *     "extendField": {
     *       "targetId": "3520432590662860801",
     *       "MMSI": 413844294
     *     },
     *     "alarmSource": 10
     *   }
     * ]
     */

    // 告警类型编码
    private String alarmType;
    // 告警时间 格式yyyy-MM-dd HH:mm:ss
    private String alarmTime;
    // 告警类型名称
    private String alarmBody;
    // 告警编码
    private String alarmCode;
    // 任务编码
    private String taskId;
    // 重要等级
    private String importanceLevel;
    // 扩展信息
    private WarningTargetSubReport extendField;
}
