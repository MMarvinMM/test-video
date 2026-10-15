package com.chinatower.event.repository;

import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;

/**
 * @author lc
 * @version v1.0.0
 * @date 2022/4/24
 */
public interface IReceiveCheckpointDeviceRepository {


    /**
     * 接收告警
     * @param receiveAlarmVO
     * @return
     */
    Result receiveCheckpointDeviceAlarm(ReceiveAlarmVO receiveAlarmVO);

}
