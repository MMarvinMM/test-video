package com.chinatower.receive.common;

import com.chinatower.event.entity.dto.AlarmSuppressConfigDTO;
import com.chinatower.event.entity.vo.AlarmOriginalPointVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.receive.vo.dto.ReceiveAlarmDTO;
import com.chinatower.receive.vo.entity.VmAlarmSuppressStrategyVO;

import java.util.List;
import java.util.Map;

/**
 * @Author: songxinfeng
 * @Date: 2024/8/21 15:49
 */
public interface CommonService {

    /**
     * 告警工单、回收站、运营研判数据入库
     * @param receiveAlarmVODB
     * @param alarmTime
     */
    void saveAlarm(ReceiveAlarmDTO receiveAlarmVODB, String alarmTime,String warningSource,String originalJson,List<AlarmOriginalPointVO> alarmPointList);

    /**
     * 告警压制
     * @param alarmVO
     * @param isIntercept 压制开关是否开启标识
     * @param strategyMap 告警压制策略配置信息
     * @param configDTOMap 雨天告警压制配置数据
     * @param intercepts  告警压制信息集合
     */
    /*boolean judgeIntercept(ReceiveAlarmVO alarmVO, String isIntercept, Map<String, List<VmAlarmSuppressStrategyVO>> strategyMap,
                        Map<String, AlarmSuppressConfigDTO> configDTOMap, List<ReceiveAlarmVO> intercepts);*/

    /**
     * 消息推送
     * @param alarms
     */
    void batchPushMessage(List<ReceiveAlarmVO> alarms);

    /**
     * 设置告警的地址和经纬度数据，AI/摄像机告警上报调用
     * @param receiveAlarmVO
     */
    //void setAlarmLocationAndAddress(ReceiveAlarmVO receiveAlarmVO);
}
