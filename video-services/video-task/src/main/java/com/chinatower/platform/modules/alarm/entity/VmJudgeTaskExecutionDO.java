package com.chinatower.platform.modules.alarm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 任务执行记录DTO
 */
@Data
public class VmJudgeTaskExecutionDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 执行记录ID
     */
    @TableId(value = "execution_id", type = IdType.AUTO)
    private Long executionId;

    /**
     * 关联任务ID
     */
    private Long taskId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 任务执行类型，1 单次研判  2自动研判
     */
    private String taskExecutionType;

    /**
     * 执行状态 0-创建, 1-执行中, 2-执行结束, 3-终止
     */
    private Integer executionStatus;

    /**
     * 开始执行时间
     */
    private Date startTime;

    /**
     * 结束执行时间
     */
    private Date endTime;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 执行日志
     */
    private String executionLog;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 删除标识 0-未删除 1-已删除
     */
    private Integer deleteFlag;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 任务组ID
     */
    private String groupId;

    /**
     * AI研判批次总数量
     */
    private Integer aiJudgeTotal;

    /**
     * AI研判真警数量
     */
    private Integer realNum;

    /**
     * AI研判假警数量
     */
    private Integer falseNum;

    /**
     * AI研判未研判数量
     */
    private Integer unJudgedNum;

    /**
     * AI研判时间
     */
    private String judgeTime;

}