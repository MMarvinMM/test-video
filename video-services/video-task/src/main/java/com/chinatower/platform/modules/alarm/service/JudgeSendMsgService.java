package com.chinatower.platform.modules.alarm.service;

/**
 * @className: JudgeSendMsgService
 * @description: 研判统计定时任务推送
 * @author: yongqi.han
 * @date: 2022/9/5
 * @requestID 需求单号及需求名称
 */
public interface JudgeSendMsgService {

    /**
     * @description: 统计待研判数量发送短信
     * @author: yongqi.han
     * @date: 2022/9/5
     * @param param
     * @return: void
     */
    String judgeSendMsg(String param);

    /**
     * @description: 查询研判短信发送开关
     * @author: yongqi.han
     * @date: 2022/10/24
     * @param
     * @return: boolean
     */
    boolean getSendMsgSwitch();

}
