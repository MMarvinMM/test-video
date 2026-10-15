package com.chinatower.platform.modules.alarm.service.impl;

import com.chinatower.platform.modules.alarm.entity.VmJudgeTaskExecutionDO;

/**
 * 执行记录信息封装类
 */
class ExecutionRecordInfo {

    /**
     * 执行记录实体
     */
    private final VmJudgeTaskExecutionDO execution;

    /**
     * 执行记录ID
     */
    private final Long executionId;

    public ExecutionRecordInfo(VmJudgeTaskExecutionDO execution, Long executionId) {
        this.execution = execution;
        this.executionId = executionId;
    }

    public VmJudgeTaskExecutionDO getExecution() {
        return execution;
    }

    public Long getExecutionId() {
        return executionId;
    }
}


