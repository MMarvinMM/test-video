package com.chinatower.device.dto;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 同步微创告警实体类
 */
@Data
public class SyncAlarmDTO implements Serializable {

    /**
     * 同步类型
     */
    private String type;

    /**
     *报警模板
     */
    private AlarmTemplateInfo alarmTemplate;

    /**
     * 区域参数
     */
    private List<AreaInfo> areaList;

    /**
     * 任务ID集合
     */
    private List<String> eventIdList;

}
