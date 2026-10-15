package com.chinatower.receive.vo.entity;

import lombok.Data;

import java.util.Date;

/**
 * 智能研判任务执行过程记录表实体类
 */
@Data
public class VmAgentOperationTaskExecutionDO {

    /**
     * 执行记录ID
     */
    private Long executionId;

    /**
     * 关联任务ID
     */
    private Long taskId;

    /**
     * 用户id
     */
    private String userId;

    private String groupId;

    /**
     * 任务执行类型，1 单次研判  2自动研判
     */
    private String taskExecutionType;

    /**
     * 执行状态 0-创建, 1-执行中, 2-执行结束, 3-停止
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
}
