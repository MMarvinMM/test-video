package com.chinatower.event.core.alarm.strategy;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.event.core.alarm.AlarmStrategyEnum;
import com.chinatower.event.core.alarm.handler.*;
import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description 物联网告警
 * @Author guyuefei
 * @Date 2022/7/7
 * @Version release_v1.5
 **/
@Component("iotAlarmStrategy")
@Slf4j
public class IotAlarmStrategy extends AlarmBaseStategy {

    @Autowired
    private IotMonitorConfigDealHandler iotMonitorConfigDealHandler;

    @Autowired
    private IotAddressAndGpsHandler iotAddressAndGpsHandler;

    @Autowired
    private IotWarningOrderHandler iotWarningOrderHandler;

    @Autowired
    private IotBatchSaveHandler iotBatchSaveHandler;

    @Autowired
    private IotSpecialHandler iotSpecialHandler;

    @Autowired
    private IotCustomersHandler iotCustomersHandler;

    @Autowired
    private IotTableHandler iotTableHandler;

    @Autowired
    private IotAlarmConfigHandler iotAlarmConfigHandler;

    @Override
    public boolean jdugeAlarmType(ReceiveAlarmVO receiveAlarmVO) {
        return WarningSourceEnum.IOT_ALARM.getTypeValue().equals(receiveAlarmVO.getAlarmSource());
    }


    @Override
    public void receiveAlarm(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        log.info("物联告警生成告警事件入参：{}", JSON.toJSONString(receiveAlarmDTO));
        /** 创建分表 **/
        iotTableHandler.setAlarmStrategy(this).setNext(iotCustomersHandler);
        /** 批量查询接收告警租户信息 **/
        iotCustomersHandler.setAlarmStrategy(this).setNext(iotAlarmConfigHandler);
        /** 处理告警配置内存信息，MongoDB因子累计值 **/
        iotAlarmConfigHandler.setAlarmStrategy(this).setNext(iotMonitorConfigDealHandler);
        /** 根据配置的物联因子阈值筛选需要生成的告警工单类型及租户,该步骤已经组装好了需要生成的告警事件 **/
        iotMonitorConfigDealHandler.setAlarmStrategy(this).setNext(iotAddressAndGpsHandler);
        /** 经纬度赋值，获取网格信息 **/
        iotAddressAndGpsHandler.setAlarmStrategy(this).setNext(iotWarningOrderHandler);
        /** 处理告警工单、告警压制、运营研判 **/
        iotWarningOrderHandler.setAlarmStrategy(this).setNext(iotBatchSaveHandler);
        /** 保存表数据 **/
        iotBatchSaveHandler.setAlarmStrategy(this).setNext(batchMessagePushHandler);
        /** 发送消息推送 **/
        batchMessagePushHandler.setAlarmStrategy(this).setNext(specialHandle());
        iotTableHandler.excute(receiveAlarmDTO);
    }
    /**
     * 执行特有处理器逻辑
     * @return
     */
    @Override
    public AbstractHandler specialHandle() {
        //物联设备联动动作处理
        return iotSpecialHandler;
    }
    @Override
    public String getStrategyName(){
        return AlarmStrategyEnum.IOT_ALARM.getAlarmStrategyBean();
    }
}
