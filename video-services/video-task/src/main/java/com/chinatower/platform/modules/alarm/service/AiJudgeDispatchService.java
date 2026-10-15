package com.chinatower.platform.modules.alarm.service;

/**
 * 告警分级研判定时调度
 */
public interface AiJudgeDispatchService {

    /**
     * 拉取未研判数据，更新为研判中并投递 Kafka
     *
     * @param limit 单次拉取数量
     */
    void dispatchPending(int limit);
}
