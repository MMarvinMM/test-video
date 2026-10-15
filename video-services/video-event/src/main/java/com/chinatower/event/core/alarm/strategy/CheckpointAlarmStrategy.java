package com.chinatower.event.core.alarm.strategy;

import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.core.alarm.AlarmStrategyEnum;
import com.chinatower.event.core.alarm.handler.AbstractHandler;
import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.repository.IReceiveCheckpointDeviceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description 处理车辆卡口摄像机告警
 * @Author guyuefei
 * @Date 2022/7/7
 * @Version release_v1.5
 **/
@Component("checkpointAlarmStrategy")
@Slf4j
public class CheckpointAlarmStrategy  extends AlarmBaseStategy {

    @Autowired
    private IReceiveCheckpointDeviceRepository receiveCheckpointDeviceRepository;

    @Override
    public boolean jdugeAlarmType(ReceiveAlarmVO receiveAlarmVO) {
        return AlarmStrategyEnum.VEHICLE_ALARM.getTypeValue().equals(receiveAlarmVO.getAlarmSource());
    }

    /**
     * 重写父类中的执行逻辑
     * @param receiveAlarmDTO
     */
    @Override
    public void receiveAlarm(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        Result result = receiveCheckpointDeviceRepository.receiveCheckpointDeviceAlarm(receiveAlarmDTO.getReceiveAlarmVO());
        if (result != null && ResultCode.SUCCESS.getCode() != result.getCode()) {
            throw new Exception("处理车辆卡口摄像机告警失败");
        }
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
        return AlarmStrategyEnum.VEHICLE_ALARM.getAlarmStrategyBean();
    }
}
