package com.chinatower.receive.service;

import com.chinatower.event.entity.dto.AlarmOperateDTO;

import java.util.List;

public interface OperateService {

    AlarmOperateDTO getOperateInfo(String warningOrderId, String userId);
    List<AlarmOperateDTO> getOperateListByOrderIds(List<String> list);

    /**
     * AI智能研判-查询待研判告警数量
     * @return
     */
    Integer getOperateAlarmCount(AlarmOperateDTO alarmOperateDTO);
}
