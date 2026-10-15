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
public class DigitalPlan implements Serializable {

    private String planId;

    private String digitalId;

    /**
     * 目标时长（天）
     */
    private Integer targetDays;

    /**
     * 阶段数量
     */
    private Integer stageCount;

    /**
     * 总体目标
     */
    private String totalTarget;

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
     * 阶段
     */
    private List<DigitalStage> digitalStage;
}
