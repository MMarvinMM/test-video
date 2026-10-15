package com.chinatower.platform.modules.alarm.service;

/**
 * @className: RemoveDelayMsgService
 * @description: 对稍后推送记录进行定期清除
 */
public interface RemoveDelayMsgService {

    /**
     * @description: 对稍后推送记录进行定期清除
     */
    void removeDelayMsg();

}
