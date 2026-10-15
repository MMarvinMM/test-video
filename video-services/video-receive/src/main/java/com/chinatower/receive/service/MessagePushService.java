package com.chinatower.receive.service;

import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.receive.vo.dto.MessagePushDTO;

import java.util.List;

public interface MessagePushService {

    MessagePushDTO batchPushMessage(List<ReceiveAlarmVO> alarms);

}
