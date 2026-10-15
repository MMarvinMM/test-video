package com.chinatower.event.core.alarm;

import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;

/**
 * @Description 处理告警定义策略接口
 * @Author guyuefei
 * @Date 2022/7/7
 * @Version release_v1.5
 **/
public interface AlarmStrategy {

    /**
     * 判断不同告警类型的方法
     * @param receiveAlarmVO
     * @return
     */
    boolean jdugeAlarmType(ReceiveAlarmVO receiveAlarmVO);

    /**
     * 处理告警核心方法
     * @param receiveAlarmDTO
     * @return
     */
    void receiveAlarm(ReceiveAlarmDTO receiveAlarmDTO) throws Exception;
}
