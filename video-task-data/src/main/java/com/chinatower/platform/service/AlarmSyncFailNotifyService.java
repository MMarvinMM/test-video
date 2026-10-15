package com.chinatower.platform.service;

import com.chinatower.platform.modules.AlarmSyncFailItem;

import java.util.List;

/**
 * 属地告警同步失败邮件提醒
 */
public interface AlarmSyncFailNotifyService {

    /**
     * 聚合发送同步失败提醒邮件（异步，整次任务只发一封聚合内容）
     *
     * @param pullDate 拉取日期 yyyyMMdd
     * @param failList 失败明细列表
     */
    void sendFailNotifyBatch(String pullDate, List<AlarmSyncFailItem> failList);
}
