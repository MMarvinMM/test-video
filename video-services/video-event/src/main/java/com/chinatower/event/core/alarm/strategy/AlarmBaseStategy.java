package com.chinatower.event.core.alarm.strategy;

import com.alibaba.fastjson.JSON;
import com.chinatower.event.core.alarm.AlarmStrategy;
import com.chinatower.event.core.alarm.handler.*;
import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description 处理告警公共父类
 * @Author guyuefei
 * @Date 2022/7/7
 * @Version release_v1.5
 **/
@Component("alarmStrategy")
@Slf4j
public abstract class AlarmBaseStategy implements AlarmStrategy {

    /** 引入公共处理执行器 **/
    @Autowired
    protected TableHandler tableHandler;
    @Autowired
    protected CustomersHandler customersHandler;
    @Autowired
    protected AddressAndGpsHandler addressAndGpsHandler;
    @Autowired
    protected SetAlarmOrderParamsHandler setAlarmOrderParamsHandler;

    @Autowired
    protected WarningOrderHandler warningOrderHandler;
    @Autowired
    protected BatchSaveHandler batchSaveHandler;

    @Autowired
    protected BatchMessagePushHandler batchMessagePushHandler;


    /**
     * 根据条件判断告警类型
     * @param receiveAlarmVO
     * @return
     */
    @Override
    public abstract boolean jdugeAlarmType(ReceiveAlarmVO receiveAlarmVO);

    /**
     * 公共处理链路
     * @param receiveAlarmDTO
     */
    @Override
    public void receiveAlarm(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        log.info("生成告警事件入参：{}", JSON.toJSONString(receiveAlarmDTO));
        /** 创建分表 **/
        tableHandler.setAlarmStrategy(this).setNext(customersHandler);
        /** 查询接收告警租户信息 **/
        customersHandler.setAlarmStrategy(this).setNext(addressAndGpsHandler);
        /** 经纬度赋值，获取网格信息 **/
        addressAndGpsHandler.setAlarmStrategy(this).setNext(setAlarmOrderParamsHandler);
        /** 设置告警参数 **/
        setAlarmOrderParamsHandler.setAlarmStrategy(this).setNext(warningOrderHandler);
        /** 处理告警工单、告警压制、运营研判 **/
        warningOrderHandler.setAlarmStrategy(this).setNext(batchSaveHandler);
        /** 保存表数据 **/
        batchSaveHandler.setAlarmStrategy(this).setNext(batchMessagePushHandler);
        /** 发送消息推送 **/
        batchMessagePushHandler.setAlarmStrategy(this).setNext(specialHandle());

        tableHandler.excute(receiveAlarmDTO);
    }

    /**
     * 各告警类型特有处理链路
     * @return
     */
    public abstract AbstractHandler specialHandle();

    /**
     * 获取处理策略名称
     * @return
     */
    public abstract String getStrategyName();
}
