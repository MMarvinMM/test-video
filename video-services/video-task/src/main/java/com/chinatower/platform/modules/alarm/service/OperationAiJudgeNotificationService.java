package com.chinatower.platform.modules.alarm.service;

public interface OperationAiJudgeNotificationService {

    /**
     * 研判环节:自动研判执行结果通知
     * @param param
     */
    void automaticJudgeResult(String param);

    /**
     * 研判环节:超期研判执行结果通知
     * @param param
     */
    void timeoutJudgeResult(String param);

    /**
     * 权重结果定时同步
     * @param param
     */
    void syncAlgorithmWeight(String param);
}
