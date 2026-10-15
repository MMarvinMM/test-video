package com.chinatower.platform.modules.alarm.service.impl;

import java.util.Date;

/**
 * 周期时间信息封装类
 */
class CycleTimeInfo {

    /**
     * 周期频次（秒）
     */
    private final int freqValue;

    /**
     * 周期开始时间
     */
    private final Date startTime;

    /**
     * 周期结束时间
     */
    private final Date endTime;

    public CycleTimeInfo(int freqValue, Date startTime, Date endTime) {
        this.freqValue = freqValue;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getFreqValue() {
        return freqValue;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }
}


