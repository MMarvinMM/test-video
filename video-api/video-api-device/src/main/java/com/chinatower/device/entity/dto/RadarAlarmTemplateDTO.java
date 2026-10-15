package com.chinatower.device.entity.dto;


import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 雷达告警模板-查询实体类
 */
@Data
public class RadarAlarmTemplateDTO extends BaseDataScopeEntity implements Serializable {

    /**
     * 告警模板ID
     */
    private Long templateId;

    /**
     * 告警模板名称
     */
    private String templateName;

    /**
     * 告警类型
     */
    private List<String> alarmTypeValueList;

    /**
     * 告警模板状态
     */
    private String templateStatus;

    /**
     * 是否关联区域
     */
    private String isUnionRegion;

    /**
     * 创建时间开始
     */
    private String createTimeBegin;

    /**
     * 创建时间结束
     */
    private String createTimeEnd;

    /**
     * 修改时间开始
     */
    private String updateTimeBegin;

    /**
     * 修改时间结束
     */
    private String updateTimeEnd;

    /**
     * 当前页
     */
    private Integer pageNum;

    /**
     * 每页条数
     */
    private Integer pageSize;

    /**
     * 同步状态
     */
    private Integer syncStatus;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务ID
     */
    private List<String> taskIdList;

    /**
     * 排序名称
     */
    private String sortName;

    /**
     * 排序规则
     */
    private String sort;

}
