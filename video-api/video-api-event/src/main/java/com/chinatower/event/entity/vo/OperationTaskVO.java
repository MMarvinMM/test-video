package com.chinatower.event.entity.vo;

import lombok.Data;

/**
 * 操作任务列表查询响应参数
 */
@Data
public class OperationTaskVO {

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 组id，每次开启任务都会重新生成
     */
    private String groupId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 任务类型
     */
    private Integer taskType;

    /**
     * 任务状态
     */
    private Integer taskStatus;

    /**
     * 频率
     */
    private String frequency;

    /**
     * 单位
     */
    private String unit;

    /**
     * 最后执行时间
     */
    private String lastExecuteTime;

    /**
     * 任务开启时间
     */
    private String startTime;


}
