package com.chinatower.receive.service;

import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.dto.ReceiveRegionAlarmDto;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.entity.vo.ReceiveSatelliteAlarmApiVo;
import com.chinatower.receive.model.ReceiveAlarmVOReq;
import com.chinatower.receive.model.TranslnglatReq;
import com.chinatower.receive.vo.entity.TranslnglatVO;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/6/27 10:32
 * @version release_v1.6.90
 */
public interface ReceiveService {

    /**
     * 添加报警事件-人工上报
     * @param receiveAlarmVO
     * @return
     */
    Result addAlarmOrder(ReceiveAlarmVO receiveAlarmVO);

    /**
     * 第三方告警推送
     * @param receiveAlarmVO
     * @return
     */
    Result addThirdAlarmOrder(List<ReceiveAlarmVO> receiveAlarmVO);

    /**
     * 接收卫星火情告警
     * @param receiveSatelliteAlarmVos
     * @return
     */
    Result receiveSatelliteAlarm(List<ReceiveSatelliteAlarmApiVo> receiveSatelliteAlarmVos);

    /**
     * 根据告警时间按创建分月表
     * @param alarmTime
     */
    void createNewTable(String alarmTime);

    /**
     * 将最新的告警时间放入Redis中
     * @param list
     */
    void setAlarmTimeToRedis(List<ReceiveAlarmVO> list);

    /**
     * 告警上报
     * @param receiveAlarmVOReq
     * @return
     */
    Result receiveAlarm(ReceiveAlarmVOReq receiveAlarmVOReq);

    /**
     * 卫片告警上报
     * @param list
     * @return
     */
    Result receiveRegionAlarm(List<ReceiveRegionAlarmDto> list);

    /**
     * 获取告警摄像机角度
     * @param req
     * @return
     * @throws Exception
     */
    TranslnglatVO getAngleByDeviceParam(TranslnglatReq req) throws Exception;

    /**
     * 根据报文中的省市区拆分数据，查询属地订购关系推送属地
     * @param receiveSatelliteAlarmVos
     */
    void sendLocalSatelliteAlarm(List<ReceiveSatelliteAlarmApiVo> receiveSatelliteAlarmVos);

    boolean isCenterNode();

}
