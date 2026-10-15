package com.chinatower.emergency.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DigitalStage implements Serializable {

    private String stageId;

    private String digitalId;

    private String planId;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 阶段名称
     */
    private String stageName;

    /**
     * 阶段时长（天）
     */
    private Integer stageDays;

    /**
     * 阶段目标
     */
    private String stageTarget;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 删除标记，0：正常，1：删除
     */
    private Integer deleteFlag;

    private String tenantId;

    private String industryCode;

    /**
     * 任务
     */
    private List<DigitalTask> taskList;
}
