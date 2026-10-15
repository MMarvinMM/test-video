package com.chinatower.platform.modules.alarm.service.impl;

import com.chinatower.platform.modules.alarm.entity.VmJudgeTaskExecutionDO;

import java.util.Date;

/**
 * 研判结果信息封装类
 */
class JudgeResultInfo {

    /**
     * 聚合后的执行记录统计结果
     */
    private final VmJudgeTaskExecutionDO updateExecution;

    /**
     * 最早研判时间
     */
    private final Date earliestDate;

    /**
     * 最晚研判时间
     */
    private final Date latestDate;

    public JudgeResultInfo(VmJudgeTaskExecutionDO updateExecution, Date earliestDate, Date latestDate) {
        this.updateExecution = updateExecution;
        this.earliestDate = earliestDate;
        this.latestDate = latestDate;
    }

    public VmJudgeTaskExecutionDO getUpdateExecution() {
        return updateExecution;
    }

    public Date getEarliestDate() {
        return earliestDate;
    }

    public Date getLatestDate() {
        return latestDate;
    }
}


