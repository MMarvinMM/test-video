package com.chinatower.event.repository;

import com.chinatower.event.entity.vo.ReceiveAlarmVO;

import java.util.List;

public interface PushThirdService {

    public void pushThirdOrder(String originalJson, List<ReceiveAlarmVO> alarmList);
}
