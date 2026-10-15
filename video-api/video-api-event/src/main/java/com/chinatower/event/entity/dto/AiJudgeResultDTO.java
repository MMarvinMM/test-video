package com.chinatower.event.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * AI研判结果查询DTO
 * 用于查询告警自动研判处理结果
 */
@Data
public class AiJudgeResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * AI研判结果筛选条件
     * 可选值：true\ false\ unjudged
     */
    private String aiJudgeResult;

    /**
     * 告警工单ID
     */
    private List<String> warningOrderIdList;

    /**
     * 任务执行记录Id
     */
    private List<Long> taskExecutionIdList;

    /**
     * 是否人工审核，1:人工审核，null：未审核
     */
    private Integer isManualReview;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;


}
