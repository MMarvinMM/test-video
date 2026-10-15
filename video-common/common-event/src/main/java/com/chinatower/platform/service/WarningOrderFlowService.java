package com.chinatower.platform.service;

import com.chinatower.event.entity.vo.ReceiveAlarmVO;

import java.util.List;
import java.util.Map;

/**
 * @Author: songxinfeng
 * @Date: 2024/8/6 11:15
 */
public interface WarningOrderFlowService {

    /**
     * 创建流程
     * @param customers
     * @return
     */
    List<Map> createRecord(List<ReceiveAlarmVO> customers);


    /**
     * 回填告警表当前环节相关字段
     * @param alarms
     * @param recordList
     */
    void setWarningLinkInfo(List<ReceiveAlarmVO> alarms, List<Map> recordList);
}
