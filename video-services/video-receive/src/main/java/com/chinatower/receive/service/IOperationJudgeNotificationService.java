package com.chinatower.receive.service;

import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.receive.vo.entity.WakeUpTowerDTO;

import java.util.List;

/**
 * AI研判通知服务接口
 */
public interface IOperationJudgeNotificationService {

    /**
     * 产生新告警唤醒小塔通知接口
     *
     * @param item 告警信息
     */
    void handleOperationJudgeNotification(ReceiveAlarmVO item, List<SysUser> needNotifyUsers);

    /**
     * 根据规则编码列表唤起小塔通知
     *
     * @param ruleCodes 规则编码列表
     * @return 是否唤起成功
     */
    WakeUpTowerDTO wakeUpTowerByRule(List<String> ruleCodes);
}
