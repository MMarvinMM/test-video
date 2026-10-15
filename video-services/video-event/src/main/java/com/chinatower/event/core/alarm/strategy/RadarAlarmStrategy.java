package com.chinatower.event.core.alarm.strategy;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.event.core.alarm.AlarmStrategyEnum;
import com.chinatower.event.core.alarm.handler.AbstractHandler;
import com.chinatower.event.core.alarm.handler.RadarAddressAndGpsHandler;
import com.chinatower.event.core.alarm.handler.RadarWarningOrderHandler;
import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description 雷达告警
 * @Author guyuefei
 * @Date 2022/7/7
 * @Version release_v1.5
 **/
@Component("radarAlarmStrategy")
@Slf4j
public class RadarAlarmStrategy extends AlarmBaseStategy {

    @Autowired
    private RadarWarningOrderHandler radarWarningOrderHandler;

    @Autowired
    private RadarAddressAndGpsHandler radarAddressAndGpsHandler;

    @Override
    public boolean jdugeAlarmType(ReceiveAlarmVO receiveAlarmVO) {
        return WarningSourceEnum.RADAR_ALARM.getTypeValue().equals(receiveAlarmVO.getAlarmSource());
    }

    @Override
    public void receiveAlarm(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        log.info("生成雷达告警事件入参：{}", JSON.toJSONString(receiveAlarmDTO));
        /** 创建分表 **/
        tableHandler.setAlarmStrategy(this).setNext(customersHandler);
        /** 查询接收告警租户信息 **/
        customersHandler.setAlarmStrategy(this).setNext(radarAddressAndGpsHandler);
        /** 经纬度赋值，获取网格信息 **/
        radarAddressAndGpsHandler.setAlarmStrategy(this).setNext(setAlarmOrderParamsHandler);
        /** 设置告警参数 **/
        setAlarmOrderParamsHandler.setAlarmStrategy(this).setNext(radarWarningOrderHandler);
        /** 处理告警工单、告警压制、运营研判 **/
        radarWarningOrderHandler.setAlarmStrategy(this).setNext(batchSaveHandler);
        /** 保存表数据 **/
        batchSaveHandler.setAlarmStrategy(this).setNext(batchMessagePushHandler);
        /** 发送消息推送 **/
        batchMessagePushHandler.setAlarmStrategy(this).setNext(specialHandle());

        tableHandler.excute(receiveAlarmDTO);
    }

    /**
     * 执行特有处理器逻辑
     * @return
     */
    @Override
    public AbstractHandler specialHandle() {
        return null;
    }

    @Override
    public String getStrategyName(){
        return AlarmStrategyEnum.RADAR_ALARM.getAlarmStrategyBean();
    }
}
